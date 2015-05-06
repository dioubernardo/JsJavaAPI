import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.json2.JSONArray;
import org.json2.JSONObject;

import br.com.jsjavaapi.Callback;
import br.com.jsjavaapi.JsJavaAPI;


public class Main {

	public static void main(String[] args) {

		PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
	//	System.setSecurityManager(null);
		
		JsJavaAPI jja = new JsJavaAPI();
		
		jja.add("getPrinters", new Callback() {
			public void run(){
				JSONArray printers = new JSONArray();
				PrintService p[] = PrinterJob.lookupPrintServices();
				for (int i=0; i<p.length;i++)
					printers.put(p[i].getName());
				JSONObject ret = new JSONObject();
				ret.put("printers", printers);
				resolve(ret);
			}
		});
		
		jja.add("filePutContents", new Callback() {
			public void run(){
				clearTimeout();
				File f = getFile(params.getString("title"), params.getString("path"), params.getString("fileName"));
				if (f == null){
					error("Operação cancelada");
					return;
				}
				
		        if (f.exists() && JOptionPane.showConfirmDialog(null, "Deseja sobreescrever esse o arquivo?\n"+f.getAbsolutePath(), "Atenção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION){
					error("Não deseja sobrescrever");
					return;
		        }

		        try{
		        	BufferedWriter out = new BufferedWriter(new FileWriter(f));
		            out.write(params.getString("content"));
		            out.close();
		            resolve(null);
		        }catch(Exception e){
		        	error(e.getMessage());
		        }
			}
		});

		jja.add("selectDirectory", new Callback() {
			public void run(){
				clearTimeout();
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle(params.getString("title"));
				fc.setApproveButtonText("Selecionar");
		        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		        if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					JSONObject ret = new JSONObject();
					ret.put("path", fc.getSelectedFile().getAbsolutePath());
					resolve(ret);
		        }else{
		        	error("Operação cancelada");	
		        }
			}
		});
		
		jja.add("print", new Callback() {
			public void run(){
				clearTimeout();
	
				try{
					boolean printed = false;
					String content = params.getString("content");
					String selectedPrinter = params.getString("printer");
					
					DocFlavor docFlavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
					Doc documentoTexto = new SimpleDoc(new ByteArrayInputStream(content.getBytes()), docFlavor, null);

					PrintRequestAttributeSet printerAttributes = new HashPrintRequestAttributeSet();
					printerAttributes.add(new JobName(params.getString("documentTitle"), null));
				
					if (selectedPrinter.equals("")){
						PrintService p[] = PrinterJob.lookupPrintServices();
						for (int i=0; i<p.length;i++){
							if (p[i].getName().equals(selectedPrinter)){
								DocPrintJob printJob = p[i].createPrintJob();
								printed = true;
								printJob.print(documentoTexto, printerAttributes);
								break;
							}
						}
					}
					
					if (!printed){
						PrintService[] servicosImpressao = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
						PrintService impressora = PrintServiceLookup.lookupDefaultPrintService();
						PrintService servico = ServiceUI.printDialog(null, -1, -1, servicosImpressao, impressora, docFlavor, printerAttributes);
						if (servico != null){ 
							DocPrintJob printJob = servico.createPrintJob(); 
							printJob.print(documentoTexto, printerAttributes);
						}
					}
					resolve(null);
				}catch(Exception e){
					error(e.getMessage());
				}						
				
			}
		});		
		
		jja.start();

	}
	
	private static File getFile(String title, String path, String fileName){
		if (path.equals("")){
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(title);
			fc.setApproveButtonText("OK");
			fc.setSelectedFile(new File(fileName));
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                return fc.getSelectedFile();
            return null;
		}else{
			return new File(path + File.separator + fileName);
		}
	} 	
	
}
