# Photo to PDF Converter (Spring Boot)

Simple Spring Boot app jo ek ya multiple photos (JPG/PNG) ko PDF me convert karta hai, using **Apache PDFBox**.

## Project Structure
```
photo-to-pdf/
├── pom.xml
├── src/main/java/com/pranshu/phototopdf/
│   ├── PhotoToPdfApplication.java      -> main entry point
│   ├── controller/PdfController.java   -> REST endpoints
│   └── service/PdfConverterService.java-> actual image->PDF logic (PDFBox)
└── src/main/resources/
    ├── application.properties
    └── static/index.html               -> browser upload page
```

## How to Run
1. Open the folder in IntelliJ IDEA (or any IDE with Maven support).
2. Make sure JDK 17+ is set as the project SDK (Spring Boot 3.x needs it).
3. Let Maven download dependencies (`spring-boot-starter-web`, `pdfbox`, `thymeleaf`).
4. Run `PhotoToPdfApplication.java`, or from terminal:
   ```
   mvn spring-boot:run
   ```
5. Open **http://localhost:8080** in your browser -> upload a photo -> PDF download ho jayega.

## API Endpoints (if you want to test with curl/Postman)

**Single photo -> single-page PDF**
```
POST /api/pdf/convert
Content-Type: multipart/form-data
field name: file
```
```
curl -F "file=@photo.jpg" http://localhost:8080/api/pdf/convert -o output.pdf
```

**Multiple photos -> one merged multi-page PDF**
```
POST /api/pdf/convert-multiple
Content-Type: multipart/form-data
field name: files (repeat for each file)
```
```
curl -F "files=@a.jpg" -F "files=@b.jpg" http://localhost:8080/api/pdf/convert-multiple -o output.pdf
```

## Notes
- Image auto-scales to fit an A4 page while keeping its aspect ratio (no stretching).
- Max upload size is 20MB per file (change in `application.properties` if needed).
- Supported formats: anything Java's `ImageIO` can read - JPG, PNG, BMP, GIF.

## Tech Stack
- Java 17
- Spring Boot 3.3.4 (spring-boot-starter-web, spring-boot-starter-thymeleaf)
- Apache PDFBox 3.0.3
