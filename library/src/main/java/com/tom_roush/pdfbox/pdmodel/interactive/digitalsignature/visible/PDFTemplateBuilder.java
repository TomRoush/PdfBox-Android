package com.tom_roush.pdfbox.pdmodel.interactive.digitalsignature.visible;

import java.io.IOException;

import com.tom_roush.pdfbox.cos.COSArray;
import com.tom_roush.pdfbox.cos.COSName;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDResources;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.common.PDStream;
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDSignatureField;
import com.tom_roush.harmony.awt.geom.AffineTransform;

import android.graphics.Bitmap;

/**
 * That class builds visible signature template which will be added in our PDF document.
 * @author Vakhtang Koroghlishvili
 */
public interface PDFTemplateBuilder
{
    /**
     * In order to create Affine Transform, using parameters
     * @param params
     */
	void createAffineTransform(byte [] params);
	
	/**
	 * Creates specified size page
	 * @param properties
	 */
	void createPage(PDVisibleSignDesigner properties);
	
	/**
	 * Creates template using page
	 * @param page
	 * @throws IOException
	 */
	void createTemplate(PDPage page) throws IOException;
	
	/**
	 * Creates Acro forms in the template
	 * @param template
	 */
	void createAcroForm(PDDocument template);
	
	/**
	 * Creates signature fields
	 * @param acroForm
	 * @throws IOException
	 */
	void createSignatureField(PDAcroForm acroForm) throws IOException;
	
	/**
	 * Creates PDSignatureField
	 * @param pdSignatureField
	 * @param page
	 * @param signatureName
	 * @throws IOException
	 */
	void createSignature(PDSignatureField pdSignatureField, PDPage page,
                                String signatureName) throws IOException;
	
	/**
	 * Create AcroForm Dictionary
	 * @param acroForm
	 * @param signatureField
	 * @throws IOException
	 */
	void createAcroFormDictionary(PDAcroForm acroForm,
                                         PDSignatureField signatureField) throws IOException;
	
	/**
	 * Creates SingatureRectangle
	 * @param signatureField
	 * @param properties
	 * @throws IOException
	 */
	void createSignatureRectangle(PDSignatureField signatureField,
                                         PDVisibleSignDesigner properties) throws IOException;
	
	/**
	 * Creates procSetArray of PDF,Text,ImageB,ImageC,ImageI    
	 */
	void createProcSetArray();
	
    /**
     * Creates signature image
     * @param template
     * @param image
     * @throws IOException
     */
	void createSignatureImage(PDDocument template, Bitmap image) throws IOException;
	
	/**
	 * 
	 * @param params
	 */
	void createFormaterRectangle(byte [] params);
	
	/**
	 * 
	 * @param template
	 */
	void createHolderFormStream(PDDocument template);
	
	/**
	 * Creates resources of form
	 */
	void createHolderFormResources();
	
	/**
	 * Creates Form
	 * @param holderFormResources
	 * @param holderFormStream
	 * @param formrect
	 */
	void createHolderForm(PDResources holderFormResources, PDStream holderFormStream,
                                 PDRectangle formrect);
	
	/**
	 * Creates appearance dictionary
	 * @param holderForml
	 * @param signatureField
	 * @throws IOException
	 */
	void createAppearanceDictionary(PDFormXObject holderForml,
                                           PDSignatureField signatureField) throws IOException;
	
	/**
	 * 
	 * @param template
	 */
	void createInnerFormStream(PDDocument template);
	
	
	/**
	 * Creates InnerForm
	 */
	void createInnerFormResource();
	
	/**
	 * 
	 * @param innerFormResources
	 * @param innerFormStream
	 * @param formrect
	 */
	void createInnerForm(PDResources innerFormResources, PDStream innerFormStream,
                                PDRectangle formrect);
	
	/**
	 * 
	 * @param innerForm
	 * @param holderFormResources
	 */
	void insertInnerFormToHolerResources(PDFormXObject innerForm,
                                                PDResources holderFormResources);
	
	/**
	 * 
	 * @param template
	 */
	void createImageFormStream(PDDocument template);
	
	/**
	 * Create resource of image form
	 */
	void createImageFormResources();
	
	/**
	 * Creates Image form
	 * @param imageFormResources
	 * @param innerFormResource
	 * @param imageFormStream
	 * @param formrect
	 * @param affineTransform
	 * @param img
	 * @throws IOException
	 */
	void createImageForm(PDResources imageFormResources, PDResources innerFormResource,
                                PDStream imageFormStream, PDRectangle formrect,
                                AffineTransform affineTransform, PDImageXObject img)
                                throws IOException;
	
	/**
	 * Inject procSetArray 
	 * @param innerForm
	 * @param page
	 * @param innerFormResources
	 * @param imageFormResources
	 * @param holderFormResources
	 * @param procSet
	 */
	void injectProcSetArray(PDFormXObject innerForm, PDPage page,
                                   PDResources innerFormResources, PDResources imageFormResources,
                                   PDResources holderFormResources, COSArray procSet);
	
	/**
	 * injects appearance streams
	 * @param holderFormStream
	 * @param innterFormStream
	 * @param imageFormStream
	 * @param imageObjectName
	 * @param imageName
	 * @param innerFormName
	 * @param properties
	 * @throws IOException
	 */
	void injectAppearanceStreams(PDStream holderFormStream, PDStream innterFormStream,
                                        PDStream imageFormStream, COSName imageObjectName,
                                        COSName imageName, COSName innerFormName,
                                        PDVisibleSignDesigner properties) throws IOException;
	
	/**
	 * just to create visible signature
	 * @param template
	 */
	void createVisualSignature(PDDocument template);
	
	/**
	 * adds Widget Dictionary
	 * @param signatureField
	 * @param holderFormResources
	 * @throws IOException
	 */
	void createWidgetDictionary(PDSignatureField signatureField,
                                       PDResources holderFormResources) throws IOException;
	
	/**
	 * 
	 * @return - PDF template Structure
	 */
	PDFTemplateStructure getStructure();
	
	/**
	 * Closes template
	 * @param template
	 * @throws IOException
	 */
	void closeTemplate(PDDocument template) throws IOException;
}
