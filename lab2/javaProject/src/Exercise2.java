import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.parser.Parser;
import org.apache.tika.config.TikaConfig;
import org.xml.sax.SAXException;
import org.apache.tika.mime.MediaType;

public class Exercise2
{

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private OptimaizeLangDetector langDetector;

    public static void main(String[] args)
    {
        Exercise2 exercise = new Exercise2();
        exercise.run();
    }

    private void run()
    {
        try
        {
            if (!new File("./outputDocuments").exists())
            {
                Files.createDirectory(Paths.get("./outputDocuments"));
            }

            initLangDetector();
            File directory = new File("./documents");
            File[] files = directory.listFiles();
            for (File file : files)
            {
                processFile(file);
            }
        } catch (IOException | SAXException | TikaException e)
        {
            e.printStackTrace();
        }

    }

    private void initLangDetector() throws IOException
    {
        this.langDetector = new OptimaizeLangDetector();
        this.langDetector.loadModels();
    }

    private void processFile(File file) throws IOException, SAXException, TikaException
    {
        InputStream stream = new FileInputStream(file);
        Parser parser = new AutoDetectParser();
        TikaConfig tika = new TikaConfig();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata =  new Metadata();
        parser.parse(stream, handler, metadata, new ParseContext());
        LanguageResult detect = langDetector.detect(handler.toString());
        String mimetype = tika.getDetector().detect(stream,metadata).toString();
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        Date crationDate =null;
        Date modificationDate=null;
        System.out.println("-----------------------");
        try {
            crationDate=format.parse(metadata.get("meta:creation-date"));
            System.out.println(crationDate);
            modificationDate=format.parse(metadata.get("Last-Modified"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveResult(file.getName(), detect.getLanguage(), metadata.get("Author"), crationDate, modificationDate, mimetype, handler.toString()); //TODO: fill with proper values
    }

    private void saveResult(String fileName, String language, String creatorName, Date creationDate,
                            Date lastModification, String mimeType, String content)
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        int index = fileName.lastIndexOf(".");
        String outName = fileName.substring(0, index) + ".txt";
        try
        {
            PrintWriter printWriter = new PrintWriter("./outputDocuments/" + outName);
            printWriter.write("Name: " + fileName + "\n");
            printWriter.write("Language: " + (language != null ? language : "") + "\n");
            printWriter.write("Creator: " + (creatorName != null ? creatorName : "") + "\n");
            String creationDateStr = creationDate == null ? "" : dateFormat.format(creationDate);
            printWriter.write("Creation date: " + creationDateStr + "\n");
            String lastModificationStr = lastModification == null ? "" : dateFormat.format(lastModification);
            printWriter.write("Last modification: " + lastModificationStr + "\n");
            printWriter.write("MIME type: " + (mimeType != null ? mimeType : "") + "\n");
            printWriter.write("\n");
            printWriter.write(content + "\n");
            printWriter.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

}