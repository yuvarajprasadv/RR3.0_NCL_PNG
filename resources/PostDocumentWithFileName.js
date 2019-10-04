main(arguments);function main(argv){              sourceDoc = app.activeDocument;        SaveDocAsPDF(sourceDoc, argv[0]);   // changed to save as jpeg        ExportDocumentAsJPEG(sourceDoc, argv[0]);        SaveSourceDoc(sourceDoc, argv[1])  }function SaveDocAsPDF (sourceDoc, pdfTargetPath){    if ( app.documents.length > 0 )     {        var pdfFileName = new File ( pdfTargetPath );        savePdfOpts = new PDFSaveOptions();        savePdfOpts.optimization = true;        savePdfOpts.preserveEditablility = false;        savePdfOpts.compatibility = PDFCompatibility.ACROBAT5;        sourceDoc.saveAs( pdfFileName, savePdfOpts );    }}function SaveSourceDoc(docSource, docTargetPath){     if ( app.documents.length > 0 )     {        var docFileName = new File ( docTargetPath );        var saveOpts = new IllustratorSaveOptions();    		saveOpts.embedLinkedFiles = false;    		saveOpts.fontSubsetThreshold = 0.0;   	 	saveOpts.pdfCompatible = true;        docSource.saveAs(docFileName, saveOpts);    }}function ExportDocumentAsJPEG(docSource, docTargetPath){	if(app.documents.length > 0)	{		var docFileSpec = new File ( docTargetPath );		var exportOptions = new ExportOptionsJPEG();		var type = ExportType.JPEG;		exportOptions.antiAliasing = false;		exportOptions.qualitysetting = 35;		exportOptions.optimization = true;		exportOptions.artBoardClipping = false;		docSource.exportFile(docFileSpec, type, exportOptions);			}}