gen-csv:
	@echo "Generating CSV files..."
	cd ./scripts
	go run generate_random_csv.go
	@echo "Done."

gcloud-login:
	gcloud auth login --update-adc

gcloud-upload:
	@echo "Enter the destination bucket:"
	@read bucket; \
	if [ -z "$$bucket" ]; then \
		bucket="ingest_data_kotlin"; \
	fi; \
	cd ./script
	gsutil cp output.csv gs://$$bucket

run:
	gradle run