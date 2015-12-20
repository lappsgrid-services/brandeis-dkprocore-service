package edu.brandeis.cs.uima;

import org.apache.commons.io.FileUtils;
import org.lappsgrid.api.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.apache.xerces.impl.io.UTF8Reader;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;

import java.io.IOException;
import java.util.Map;

import static org.lappsgrid.discriminator.Discriminators.Uri;



public abstract class AbstractWebService implements WebService {

    protected static final Logger logger = LoggerFactory
            .getLogger(AbstractWebService.class);

    @Override
    /**
     * This is default execute: takes a json, wrap it as a LIF, run modules
     */
    public String execute(String input) {
        if (input == null)
            return null;
        input = input.trim();  // remove the whitespace.
        Data data =  null;
        if(input.startsWith("{") && input.endsWith("}")) {
            data = Serializer.parse(input, Data.class);
        } else {
            data = new Data();
            data.setDiscriminator(Uri.TEXT);
            data.setPayload(input);
        }

        final String discriminator = data.getDiscriminator();
        Container cont;

        switch (discriminator) {
            case Uri.ERROR:
                return input;
            case Uri.JSON_LD:
                cont = new Container((Map) data.getPayload());
                break;
            case Uri.LIF:
                cont = new Container((Map) data.getPayload());
                break;
            case Uri.TEXT:
                cont = new Container();
                cont.setText((String) data.getPayload());
                cont.setLanguage("en");
                break;
            default:
                String message = String.format
                        ("Unsupported discriminator type: %s", discriminator);
                return new Data<>(Uri.ERROR, message).asJson();
        }

        try {
            cont.setContext(Container.REMOTE_CONTEXT);
            return execute(cont);
        } catch (Throwable th) {
            th.printStackTrace();
            String message =
                    String.format("Error processing input: %s", th.toString());
            return new Data<>(Uri.ERROR, message).asJson();
        }
    }

    /**
     * This will be overridden for each module
     */
    public abstract String execute(Container json)
            throws UimaServiceException;


    public String getTemplate() {
        try {
            String serviceName = this.getClass().getName();
            String resName = "/template/" + serviceName + ".dsl";
            String dsl = FileUtils.readFileToString(new File(this.getClass().getResource(resName).toURI()), "UTF-8");
            return dsl;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getMetadata() {
        String metadata = "{}";
        String serviceName = this.getClass().getName();
        String resName = "/metadata/" + serviceName + ".json";
        logger.info("load resources:" + resName);
        InputStream inputStream = this.getClass().getResourceAsStream(resName);

        if (inputStream == null) {
            String message = "Unable to load metadata file for " + this.getClass().getName();
            logger.error(message);
        } else {
            UTF8Reader reader = new UTF8Reader(inputStream);
            try {
                Scanner s = new Scanner(reader).useDelimiter("\\A");
                String metadataText = s.hasNext() ? s.next() : "";
                metadata = (new Data<>(Uri.META,
                        Serializer.parse(metadataText, ServiceMetadata.class))).asPrettyJson();
            } catch (Exception e) {
                String message = "Unable to parse json for " + this.getClass().getName();
                logger.error(message, e);
                metadata = (new Data<>(Uri.ERROR, message)).asPrettyJson();
            }
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return metadata;
    }
}
