package nx.peter.java;

import nx.peter.java.document.core.Document;
import nx.peter.java.document.reader.DocumentReader;
import nx.peter.java.document.type.Book;
import nx.peter.java.document.writer.DocumentWriter;
import nx.peter.java.document.writer.Page;
import nx.peter.java.document.writer.page.body.List;
import nx.peter.java.json.reader.JsonObject;
import nx.peter.java.util.DateManager;
import nx.peter.java.util.data.encryption.Password;

public class Main {
    public static void main(String[] args) {
        println("Hello world!");

        /*
        Document<?> document = new Book()
                .setAuthor("Uareime P. E.")
                .setFilePath("breakthrough")
                .setTitle("Breakthrough From Despair")
                .setDateOfPublication(DateManager.getCurrentDate())
                .setSerialNumber(Password.generate(8, Password.Restriction.AlphaNumeric).get());

        List<String, ?, ?> content = new nx.peter.java.document.core.page.body.List.Creator<>();
        content.add("i. Dedication",
                "ii. Preface",
                "iii. Acknowledgement",
                "1. Introduction",
                "2. A New Dawn",
                "3. Behind The Blue Veil",
                "4. Into the Shackles of Shame",
                "5. At Last a Smile",
                "6. Welcome to Peace Kingdom");

        Page<?, ?, ?> page = new Page.Creator()
                .setHeading("Table of Content".toUpperCase())
                .setNumber(1)
                .setBody(content);

        document.addPages(page);

        println();
        println(document);
        println();

        boolean stored = new DocumentWriter(document)
                .setPath(document.getFilePath())
                .storeAsJson();

        println(stored);
         */

        nx.peter.java.document.reader.Document<?, ?> document = new  DocumentReader("breakthrough.json", Document.Type.Book)
                .getDocument();

        JsonObject<?> json = document.toJson();

        println();
        println(document.getPages().toJson().getPrettyPrinter());

        println();
        // println(json.getPrettyPrinter());

    }

    public static void println() {
        println("");
    }

    public static void print(Object object) {
        System.out.print(object);
    }

    public static void println(Object object) {
        System.out.println(object);
    }

}