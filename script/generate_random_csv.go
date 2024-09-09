package main

import (
	"encoding/csv"
	"fmt"
	"math/rand"
	"os"
	"sync"
	"time"
)

const (
	numRows      = 2000000
	numWorkers   = 20
	csvFileName  = "output.csv"
	charset      = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
	fileNameBase = "file_"
)

func randomString(length int) string {
	b := make([]byte, length)
	for i := range b {
		b[i] = charset[rand.Intn(len(charset))]
	}

	return string(b)
}

func generateRow(id int) []string {
	return []string{
		randomString(10), // A
		randomString(10), // B
		randomString(10), // C
		randomString(10), // D
		fmt.Sprintf("%s%d.csv", fileNameBase, id), // _filename
		time.Now().Format(time.RFC3339Nano),       // _inserted_at
	}
}

func worker(id int, jobs <-chan int, results chan<- []string, wg *sync.WaitGroup) {
	defer wg.Done()
	for job := range jobs {
		results <- generateRow(job)
	}
}

func writeCSV(header []string, results <-chan []string, wg *sync.WaitGroup) {
	defer wg.Done()

	file, err := os.Create(csvFileName)
	if err != nil {
		fmt.Println("Error creating file:", err)
		return
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	writer.Write(header)

	for row := range results {
		writer.Write(row)
	}
}

// TOOD: fix deadlock
func main() {
	rand.Seed(time.Now().UnixNano())

	jobs := make(chan int, numRows)
	results := make(chan []string, numRows)

	var wg sync.WaitGroup

	header := []string{"A", "B", "C", "D", "_filename", "_inserted_at"}

	wg.Add(numWorkers + 1)

	// Start workers
	for w := 0; w < numWorkers; w++ {
		go worker(w, jobs, results, &wg)
	}

	// Start CSV writer
	go writeCSV(header, results, &wg)

	// Send jobs to workers
	for j := 0; j < numRows; j++ {
		jobs <- j
	}
	close(jobs)

	// Wait for all workers and writer to finish
	wg.Wait()
	close(results)

	fmt.Println("CSV file generated successfully!")
}
