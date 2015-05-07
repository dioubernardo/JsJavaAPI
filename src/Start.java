import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.swing.ProgressMonitorInputStream;

public class Start {

	public static void main(String[] args) {
		try {
			URL url = new URL(
					"http://localhost/JsJavaAPI/build/JsJavaAPI-server.jar");
			ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(
					null, "Baixando aplicativo", url.openStream());
			InputStream in = new BufferedInputStream(pmis);

			File temp = File.createTempFile("jsjavaapi", ".jar");
			temp.deleteOnExit();
			
			FileOutputStream out = new FileOutputStream(temp);

			byte[] buffer = new byte[1024];
			int count;
			while ((count = in.read(buffer, 0, 1024)) > 0) {
				out.write(buffer, 0, count);
			}
			out.close();
			in.close();

			Process p = Runtime.getRuntime().exec(
					"\""+System.getProperty("java.home")+"\\bin\\java\" -jar \"" + temp.getAbsolutePath() + "\"");
			p.waitFor();
			
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
