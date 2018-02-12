package com.epsilon.smarthome.core.services.impl;
import java.util.Calendar; 
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
    
    
import javax.jcr.Repository; 
import javax.jcr.SimpleCredentials; 
import javax.jcr.Node; 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
     
import org.apache.jackrabbit.commons.JcrUtils;
    
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
    
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import javax.jcr.RepositoryException;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.commons.JcrUtils;
    
import javax.jcr.Session;
import javax.jcr.Node;  
  
//PDFBOX 
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage; 
import org.apache.pdfbox.pdmodel.font.PDFont; 
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDStream; 
  
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory ; 
import org.apache.sling.api.resource.ResourceResolver; 
import org.apache.sling.api.resource.Resource; 
 
//AssetManager
import com.day.cq.dam.api.AssetManager;
import com.epsilon.smarthome.core.services.PDFService; 
//This is a component so it can provide or consume services
@Component
   
@Service
public class PDFServiceImpl implements PDFService {
  
      
/** Default log. */
protected final Logger log = LoggerFactory.getLogger(this.getClass());
           
private Session session;
               
//Inject a Sling ResourceResolverFactory
@Reference
private ResourceResolverFactory resolverFactory;
      
@Override
public String createPDF(String filename,String value) {
// This custom AEM service creates a PDF document using PDFBOX API and stores the PDF in the AEM JCR
          
/*try
{
	ITextRenderer renderer = new ITextRenderer();

    // if you have html source in hand, use it to generate document object
    renderer.setDocumentFromString( "<html><body><strong>Hello</strong> <em>world</em>!</body></html>" );
    renderer.layout();

    String fileNameWithPath = "C:/Users/Goran/Documents/Documents/Development/workspace/FlyingSaucer/data/output/" + "PDF-FromHtmlString.pdf";
    FileOutputStream fos = new FileOutputStream( fileNameWithPath );


    System.out.println( "File 2: '" + fileNameWithPath + "' created." );
              
    //Save the PDF into the AEM DAM
    ByteArrayOutputStream out = new ByteArrayOutputStream();
                  
    renderer.createPDF(out);
                  
    byte[] myBytes = out.toByteArray(); 
        InputStream is = new ByteArrayInputStream(myBytes) ; 
    String damLocation = writeToDam(is,filename);
    out.close();
              
    //....
    return damLocation; 
    }
catch(Exception e)
{
    e.printStackTrace();
}
    return null;
}
      
      
//Save the uploaded file into the Adobe CQ DAM
private String writeToDam(InputStream is, String fileName)
{
    try
    {
    //Invoke the adaptTo method to create a Session used to create a QueryManager
    ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
      
  //Use AssetManager to place the file into the AEM DAM
    com.day.cq.dam.api.AssetManager assetMgr = resourceResolver.adaptTo(com.day.cq.dam.api.AssetManager.class);
    String newFile = "/content/dam/pdf/"+fileName ; 
    assetMgr.createAsset(newFile, is, "application/pdf", true);
     
    log.info("THE PDF Asset was placed into the DAM");       
  
               
    // Return the path to the document that was stored in CRX. 
    return newFile;
}
catch(Exception e)
{
    e.printStackTrace();
}*/
return null; 
}
}
