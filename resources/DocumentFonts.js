function getUsedFonts (doc){	try	{	     var xmlString = new XML(doc.XMPString);	     fontsInfo = xmlString.descendants("stFnt:fontName");	     var ln = fontsInfo.length(), arr = [];	     for (var i = 0; i<ln; i++){arr.push(fontsInfo[i])};	     return arr;	 }	 catch (e)	 {	 	return "error";	 }}var docFontsList = getUsedFonts(activeDocument);	main();    function main(){	return docFontsList;}