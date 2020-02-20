package com.w3engineers.ecommerce.bootic.ui.invoice;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseActivity;
import com.w3engineers.ecommerce.bootic.data.helper.models.AddressModel;
import com.w3engineers.ecommerce.bootic.data.helper.models.CustomProductInventory;
import com.w3engineers.ecommerce.bootic.data.helper.models.InvoiceModel;
import com.w3engineers.ecommerce.bootic.data.helper.response.UserRegistrationResponse;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;
import com.w3engineers.ecommerce.bootic.databinding.ActivityInvoiceBinding;
import com.w3engineers.ecommerce.bootic.ui.pdfshow.PdfShowActivity;
import com.w3engineers.ecommerce.bootic.ui.pdfshow.PdfShowMvpView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

public class InvoiceActivity extends BaseActivity<InvoiceMvpView,InvoicePresenter> {

    ActivityInvoiceBinding mBinding;
    String transactionId,customerName,customerAddress,totalPrice,
            totalTax,subTotalPrice,invoiceDate;
    private InvoiceMainAdapter mAdapter;
    private List<CustomProductInventory> inventoryList=new ArrayList<>();
    private File pdfFile;
    float mHeadingFontSize = 40.0f;
    float mParagraphFontSize = 20.0f;
    Font mInvoiceTitleFontBlack = new Font(Font.FontFamily.TIMES_ROMAN, mHeadingFontSize, Font.BOLD, BaseColor.BLACK);
    Font mParagraphFontDarkGray = new Font(Font.FontFamily.TIMES_ROMAN, mParagraphFontSize, Font.NORMAL, BaseColor.DARK_GRAY);
    Font mParagraphFontBlack = new Font(Font.FontFamily.TIMES_ROMAN, mParagraphFontSize, Font.NORMAL, BaseColor.BLACK);


    @Override
    protected int getLayoutId() {
        return R.layout.activity_invoice;
    }

    @Override
    protected void startUI() {
        mBinding= (ActivityInvoiceBinding) getViewDataBinding();
        initToolbar();
        UserRegistrationResponse loggedInUser = CustomSharedPrefs.getLoggedInUser(this);
        if (loggedInUser !=null){
            customerName=loggedInUser.userRegistrationInfo.username;
            String address = SharedPref.getSharedPref(this).read(Constants.Preferences.USER_ADDRESS);
            AddressModel addressModel = UtilityClass.stringToAddressModel(address);
            if (addressModel != null) {
                if (addressModel.addressLine2 != null){
                    customerAddress = addressModel.addressLine1 + addressModel.addressLine2
                            + getResources().getString(R.string.city_text) + addressModel.city +
                            getResources().getString(R.string.zip_code) + addressModel.zipCode
                            + getResources().getString(R.string.state_text) + addressModel.state +
                            getResources().getString(R.string.country_text) + addressModel.country;
                }else {
                    customerAddress = addressModel.addressLine1
                            + getResources().getString(R.string.city_text) + addressModel.city +
                            getResources().getString(R.string.zip_code) + addressModel.zipCode
                            + getResources().getString(R.string.state_text) + addressModel.state +
                            getResources().getString(R.string.country_text) + addressModel.country;
                }

            }
        }

        transactionId=SharedPref.getSharedPref(this).read(Constants.Preferences.TRANSACTION_ID);
        totalPrice=SharedPref.getSharedPref(this).read(Constants.Preferences.INVOICE_TOTAL_AMOUNT);
        totalTax=SharedPref.getSharedPref(this).read(Constants.Preferences.INVOICE_TAX);
        subTotalPrice=SharedPref.getSharedPref(this).read(Constants.Preferences.INVOICE_SUB_TOTAL_AMOUNT);
        invoiceDate=SharedPref.getSharedPref(this).read(Constants.Preferences.CURRENT_DATE);
        initAdapter();
        getDataFromSharedPref();
        setDataToInvoiceView();
        setClickListener(mBinding.buttonGeneratePdf);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.button_generate_pdf:
                createPdfWrapper();
                Log.d("EnterPdf","onClick");
                break;

        }
    }


    /**
     * Take permission
     */
    private void createPdfWrapper() {

        Dexter.withActivity(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                try {
                    createPdf();
                    Log.d("EnterPdf","onPermissionGranted");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("EnterPdf","FileNotFoundException");
                } catch (DocumentException e) {
                    e.printStackTrace();
                    Log.d("EnterPdf","DocumentException");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                // Permission Denied
                Log.d("EnterPdf","PermissionDeniedResponse");
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();
    }


    /**
     * Create pdf
     *
     * @throws FileNotFoundException File Not Found Exception
     * @throws DocumentException     Document Exception
     */
    private void createPdf() throws IOException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory() +
               "/Bootic");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        pdfFile = new File(docsFolder.getAbsolutePath(), "bootic_invoice_"+timeStamp+".pdf");
        Document document = new Document();
        // Location to save
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile)); //todo
        // Open to write
        document.open();
        writeDataToPDF(document);

        Log.d("EnterPdf","docsFolder");

    }

    /**
     * Write data to pdf
     *
     * @param document document
     * @throws DocumentException
     */
    private void writeDataToPDF(Document document) throws DocumentException, IOException {

        document.add(new Paragraph("\n"));
        addHeadingCompanyTitle(document, getResources().getString(R.string.app_name));
       // setImageInPdf(document);
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));
        addParagraphWithDifferentColor(document,this.getResources().getString(R.string.txn_id)+transactionId);
        addParagraphWithDifferentColor(document,this.getResources().getString(R.string.customer_name)+customerName);
        addParagraphWithDifferentColor(document, totalPrice);

        lineSeparator(document);
        addParagraph(document, this.getResources().getString(R.string.invoice_date));
        addParagraphWithDifferentColor(document, invoiceDate);

        // LINE SEPARATOR
        lineSeparator(document);


        addParagraph(document, this.getResources().getString(R.string.billing_address));
        addParagraphWithDifferentColor(document, customerAddress);

        // LINE SEPARATOR
        lineSeparator(document);

        //item and product
        addParagraphLeftAndRight(document, this.getResources().getString(R.string.items),this.getResources().getString(R.string.amount));

        lineSeparator(document);

        for (CustomProductInventory productInventory : inventoryList) {
            double price = Double.valueOf(productInventory.currentQuantity) * Double.valueOf( productInventory.price);
            addParagraphLeftAndRight(document, productInventory.productName,
                     UtilityClass.getCurrencySymbolAndAmount(this,(float)price));
            addParagraph(document, ""+setSizeColor(productInventory));
            addParagraph(document, ""+productInventory.currentQuantity+" X "+
                  UtilityClass.getCurrencySymbolAndAmount(this,productInventory.price));
            lineSeparator(document);
        }

        //price
        addParagraphWithAlignRight(document, this.getResources().getString(R.string.sub_total)+subTotalPrice);
        addParagraphWithAlignRight(document, this.getResources().getString(R.string.tax_invoice)+totalTax);

        lineSeparator(document);
        addParagraphWithAlignRight(document, this.getResources().getString(R.string.total_invoice)+totalPrice);
        lineSeparator(document);
        document.add(new Paragraph("\n"));
        addParagraphWithAlignLeft(document, this.getResources()
                .getString(R.string.if_you_have_any_further_queries_please_do_not_hesitate_to_contact_us));
        Log.d("EnterPdf","writeDataToPDF");
        document.close();
        previewPdf();
    }



    private StringBuilder setSizeColor(CustomProductInventory productInventory){
        StringBuilder attTitle = new StringBuilder();
         List<String> titleList = new ArrayList<>();
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        titleList = new Gson().fromJson(productInventory.attributeTitle, listType);

        if (productInventory.attributeTitle != null && !productInventory.attributeTitle.isEmpty()) {
            for (String title : titleList) {
                attTitle.append(title.trim());
                if (!title.contains("Size:")){
                    attTitle.append(this.getResources().getString(R.string.coma));
                }
            }
        }
        return attTitle;
    }



    /**
     * Line separator
     * @param document document
     * @throws DocumentException
     */
    private void lineSeparator(Document document) throws DocumentException {
        document.add(new Paragraph("\n"));
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(lineSeparator);
    }


    private void setImageInPdf(Document document) throws DocumentException, IOException {
        try {
            // get input stream
            InputStream ims = getAssets().open("");
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setAbsolutePosition(450f, 10f);
            document.add(image);
        }
        catch(IOException ex)
        {
            return;
        }
    }


    /**
     * Add Heading
     *
     * @param document document
     * @param data     add Heading Title
     * @throws DocumentException
     */
    private void addHeadingTitle(Document document, String data) throws DocumentException {

        // Adding Title....
        // Creating Chunk
        Chunk mInvoiceTitleChunk = new Chunk(data, mInvoiceTitleFontBlack);
        // Creating Paragraph to add...
        Paragraph mInVoiceTitleParagraph = new Paragraph(mInvoiceTitleChunk);
        // Setting Alignment for Heading
        mInVoiceTitleParagraph.setAlignment(Element.ALIGN_RIGHT);
        // Finally Adding that Chunk
        document.add(mInvoiceTitleChunk);

    }



    /**
     * Add Heading
     *
     * @param document document
     * @param data     add Heading Title
     * @throws DocumentException
     */
    private void addHeadingCompanyTitle(Document document, String data) throws DocumentException {

        Chunk glue = new Chunk(new VerticalPositionMark());
        Paragraph p = new Paragraph(data,mInvoiceTitleFontBlack); //Text to the left
        p.add(new Chunk(glue));
        p.add(getResources().getString(R.string.invoice)); //Text to the right
        document.add(p);

    }

    /**
     * Add paragraph Left and right together
     */
    private void addParagraphLeftAndRight(Document document, String left,String right) throws DocumentException {
        Chunk glue = new Chunk(new VerticalPositionMark());
        Paragraph p = new Paragraph(left,mParagraphFontDarkGray); //Text to the left
        p.add(new Chunk(glue));
        p.add(right); //Text to the right
        document.add(p);

    }


    /**
     * Add paragraph
     * @param document document
     * @param data     add Paragraph
     * @throws DocumentException
     */
    private void addParagraph(Document document, String data) throws DocumentException {
        Chunk mTransChunk = new Chunk(data, mParagraphFontDarkGray);
        Paragraph mTransParagraph = new Paragraph(mTransChunk);
        document.add(mTransParagraph);
    }


    /**
     * Add paragraph
     * @param document document
     * @param data     add Paragraph
     * @throws DocumentException
     */
    private void addParagraphWithDifferentColor(Document document, String data) throws DocumentException {
        Chunk mTransChunk = new Chunk(data, mParagraphFontBlack);
        Paragraph mTransParagraph = new Paragraph(mTransChunk);
        document.add(mTransParagraph);
    }


    /**
     * Add paragraph
     * @param document document
     * @param data     add Paragraph
     * @throws DocumentException
     */
    private void addParagraphWithAlignRight(Document document, String data)
            throws DocumentException {
        Chunk mTransChunk = new Chunk(data, mParagraphFontDarkGray);
        Paragraph mTransParagraph = new Paragraph(mTransChunk);
        mTransParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(mTransParagraph);
    }


    /**
     * Add paragraph
     * @param document document
     * @param data     add Paragraph
     * @throws DocumentException
     */
    private void addParagraphWithAlignLeft(Document document, String data)
            throws DocumentException {
        Chunk mTransChunk = new Chunk(data, mParagraphFontDarkGray);
        Paragraph mTransParagraph = new Paragraph(mTransChunk);
        mTransParagraph.setAlignment(Element.ALIGN_LEFT);
        document.add(mTransParagraph);
    }

    /**
     * Show pdf
     */
    private void previewPdf() {
 /*      PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(InvoiceActivity.this,
                    this.getApplicationContext().getPackageName()+ ".provider", pdfFile);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            Log.d("EnterPdf","previewPdf");
        } else {
            Toast.makeText(this, getResources().getString(R.string.download_pdf_viewer), Toast.LENGTH_SHORT).show();
        }*/
        PdfShowActivity.runActivity(this,pdfFile);



    }





    /**
     * toolbar initialization
     */
    private void initToolbar() {
        TextView toobarTitle = findViewById(R.id.toolbar_title);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toobarTitle.setText(getString(R.string.invoice));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Set data to invoice view
     */
    private void setDataToInvoiceView(){
        mBinding.textTransacation.setText(this.getResources().getString(R.string.txn_id)+transactionId);
        mBinding.textName.setText(this.getResources().getString(R.string.customer_name)+customerName);
        mBinding.textCustomerAddress.setText(customerAddress);
        mBinding.textSubtotal.setText(this.getResources().getString(R.string.sub_total)+subTotalPrice);
        mBinding.textTax.setText(this.getResources().getString(R.string.tax_invoice)+totalTax);
        mBinding.textBalanceAmount.setText(totalPrice);
        mBinding.textDateInvoiceValue.setText(invoiceDate);
        mBinding.textTotalPrice.setText(this.getResources().getString(R.string.total_invoice)+totalPrice);
       // mBinding.textCompanyName.setText(companyName);
      //  mBinding.textCompanyAddress.setText(addressOfCompany);
    }

    private void getDataFromSharedPref(){
        String productList=SharedPref.getSharedPref(this).read(Constants.Preferences.INVOICE_PRODUCT_LIST);
        InvoiceModel invoiceModel=UtilityClass.jsonToInvoice(productList);
        if (invoiceModel.inventoryListForInvoice !=null){
            inventoryList=invoiceModel.inventoryListForInvoice;
            if ( inventoryList.size() > 0) {
                mAdapter.addItem(inventoryList);
            }
        }


    }

    private void initAdapter(){
        mAdapter=new InvoiceMainAdapter(new ArrayList<>(),this);
        mBinding.recyclerViewInvoice.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerViewInvoice.setAdapter(mAdapter);
    }


    @Override
    protected void stopUI() {

    }

    @Override
    protected InvoicePresenter initPresenter() {
        return new InvoicePresenter();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //toast
        Toast.makeText(this, this.getResources().getString(R.string.order_page),Toast.LENGTH_LONG).show();
    }
}
