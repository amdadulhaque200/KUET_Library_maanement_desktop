# KUET Library Management Desktop

This project is a simple JavaFX desktop app.

Requirements
- JDK 17+
- Maven (optional but recommended) or run from an IDE that supports JavaFX (IntelliJ IDEA / Eclipse)
- Internet access to download JavaFX Maven dependencies when building

Quick run (recommended)
1. From project root open PowerShell and run:

   mvn -DskipTests package
   mvn javafx:run

If you don't have Maven installed, run from your IDE (IntelliJ): Import the project as Maven, then run `LibraryApplication`.

Alternative: Use run.bat (tries to call mvn)

Security / notes
- The project uses a small Navigation helper that loads FXML in a BOM-safe way (strips UTF-8 BOM) to avoid FXML parse errors caused by BOM.

If you run into an error, paste the console output here and I'll fix it.

