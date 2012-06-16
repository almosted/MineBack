package fr.almosted.MineBack;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//Main Class + Fenetre
public class MineBack extends JFrame {

	private static final long serialVersionUID = 1L;

	Image favicon = new ImageIcon(this.getClass().getResource("favicon.png")).getImage();
	
	MineBackPanel mp = new MineBackPanel();

	public MineBack() {
		this.setTitle("MineBack");
		this.setIconImage(favicon);
		this.setSize(300, 150);
		this.setResizable(false);
		this.setVisible(true);
		this.setContentPane(mp);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		mp.drawPanel(0);
		mp.types.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!mp.types.getSelectedItem().toString().equals(" ")) {
					mp.drawPanel(2);
				}
			}
		});
		mp.install.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				progress("http://tweet-mod.com/launcher/jars/" + mp.versions.getSelectedItem().toString() + " - " + mp.types.getSelectedItem().toString() + "/minecraft.jar");
				mp.removeAll();
			}
		});
		mp.restore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mp.restore();
				mp.restore.setEnabled(false);
				mp.restore.setVisible(false);
			}
		});
	}
	
	public void progress(final String url) {
		mp.visible = true;
	      Thread t = new Thread() {
	          public void run() {
	      		mp.installVersionMc(url);
	          }
	        };
	        t.start();
	}

	public static void main(String[] args) {
		new MineBack();
	}

//Panel
public class MineBackPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	Font font = new Font("Arial", Font.PLAIN, 11);
	
	Image dirt = new ImageIcon(this.getClass().getResource("dirt.png")).getImage();
	
	File minecraft = new File(OsInfos.getWorkingDirectory("minecraft") + File.separator + "bin" + File.separator + "minecraft.jar");
	File minecraftb = new File(OsInfos.getWorkingDirectory("minecraft") + File.separator + "bin" + File.separator + "minecraftBack.jar");
	
	JLabel ltypes = new JLabel("Types:");
	JLabel lversions = new JLabel("Version:");
	JLabel ldone = new JLabel();
	JLabel mineback = new JLabel("MineBack - v1.1");
	
	JButton install = new JButton("Install");
	JButton restore = new JButton("Restore");
	
	JComboBox types = new JComboBox(getTypes());
	JComboBox versions = new JComboBox(getVersions());
	
	Color green = new Color(0, 128, 0);
	Color lightgreen = new Color(32, 160, 32);
	
	int posX = 0;
	
	boolean visible = false;
	
	public void paintComponent(Graphics g) {
		for(int x = 0; x <= this.getWidth()/64; x++) {
			for(int y = 0; y <= this.getHeight()/64; y++) {
				g.drawImage(dirt, x * 64, y * 64, 64, 64, this);
			}
		}
		
		ltypes.setForeground(Color.white);
		lversions.setForeground(Color.white);
		ldone.setForeground(Color.white);
		mineback.setForeground(Color.white);
		install.setSize(75, 20);
		restore.setSize(75, 20);
		
		ltypes.setFont(font);
		types.setFont(font);
		lversions.setFont(font);
		ldone.setFont(font);
		versions.setFont(font);
		install.setFont(font);
		restore.setFont(font);
		mineback.setFont(font);
		
		ldone.setLocation(20, 75);
		ltypes.setLocation(28, 20);
		types.setLocation(65, 17);
		lversions.setLocation(20, 50);
		versions.setLocation(65, 47);
		mineback.setLocation(0, -3);
		install.setLocation(this.getWidth() - 95, this.getHeight() - 30);
		restore.setLocation(20, this.getHeight() - 30);
		
		if(visible) {
			g.setColor(Color.black);
			g.fillRect(10, this.getHeight()/2 - 5, this.getWidth() - 20, 10);
			
			g.setColor(green);
			g.fillRect(10, this.getHeight()/2 - 5, getPosX(), 8);
			
			g.setColor(lightgreen);
			g.fillRect(12, this.getHeight()/2 - 3, getPosX() - 4, 2);
		}
	}
	
	public int getPosX() {
		return this.posX;
	}
	
	public void setPosX(int i) {
		this.posX = i;
	}

	public void drawPanel(int i) {
		add(ltypes);
		add(types);
		add(ldone);
		add(mineback);
		if(minecraftb.exists()) {
			add(restore);
		}
		if(i == 2) {
			versions = new JComboBox(getVersions());
			types.setEnabled(false);
			add(lversions);
			add(versions);
			add(install);
			validate();
		}
	}
	
	public void restore() {
		if(minecraftb.exists()) {
			if(minecraft.exists()) {
				minecraft.delete();
			}
			minecraftb.renameTo(minecraft);
			ldone.setText("Restore with success!");
		}
	}
	
	public void installVersionMc(String adresse) {
		if(!minecraftb.exists() && minecraft.exists()) {
			minecraft.renameTo(minecraftb);
		}
		
		adresse = adresse.replaceAll(" ", "%20");
		
		BufferedReader reader = null;
		FileOutputStream fos = null;
		InputStream in = null;
			
		try {
			URL url = new URL(adresse);
			URLConnection conn = url.openConnection();			
			int FileLenght = conn.getContentLength();
			if (FileLenght == -1) {
				throw new IOException("Erreur");
			}
			in = conn.getInputStream();
			reader = new BufferedReader(new InputStreamReader(in));
			fos = new FileOutputStream(minecraft);
			byte[] buff = new byte[1024];
			int l = in.read(buff);
			int percents = 0;
			
			while (l > 0) {
				percents = (((int) minecraft.length())*274)/FileLenght;
				fos.write(buff, 0, l);
				l = in.read(buff);
				setPosX(percents);
				mp.repaint();
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				fos.flush();
				fos.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try {
				reader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		mp.types.setEnabled(true);
		mp.restore.setEnabled(true);
		mp.restore.setVisible(true);
		mp.visible = false;
		mp.ldone.setText("Install finish!");
		mp.drawPanel(1);
		mp.repaint();
	}
	
	private ComboBoxModel getTypes() {
		DefaultComboBoxModel cmTypes = new DefaultComboBoxModel();
		URL url = null;
		try {
			url = new URL("http://tweet-mod.com/launcher/types.txt");
		}
		catch (MalformedURLException e1){
			e1.printStackTrace();
		}
		
		InputStreamReader ipsr = null;
		try {
			ipsr = new InputStreamReader(url.openStream());
		}
		catch (IOException e1){
			e1.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(ipsr);
		
		String v = null;
		
		StringBuffer buffer = new StringBuffer();
		
		try{
			while ((v = br.readLine()) != null){
			buffer.append(v);
			cmTypes.addElement(v);
			}
			br.close();
		}
		catch (IOException e){
		e.printStackTrace();
		} 
		return cmTypes;
	}
	
	private ComboBoxModel getVersions() {
		DefaultComboBoxModel cm = new DefaultComboBoxModel();
		URL url = null;
		try {
			url = new URL("http://tweet-mod.com/launcher/versions.txt");
		}
		catch (MalformedURLException e1){
			e1.printStackTrace();
		}
		
		InputStreamReader ipsr = null;
		try {
			ipsr = new InputStreamReader(url.openStream());
		}
		catch (IOException e1){
			e1.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(ipsr);
		
		String v = null;
		
		StringBuffer buffer = new StringBuffer();
		
		try{
			while ((v = br.readLine()) != null){
			buffer.append(v);
			if(v.contains(types.getSelectedItem().toString())) {
				v = v.replaceAll(" - " + types.getSelectedItem().toString(), "");
				cm.addElement(v);
			}
			}
			br.close();
		}
		catch (IOException e){
		e.printStackTrace();
		} 
		return cm;
	}

}

//Infos OS
public static class OsInfos
{
  private static File workDir = null;

  public static File getWorkingDirectory() {
    if (workDir == null) workDir = getWorkingDirectory("minecraft");
    return workDir;
  }

  public static File getWorkingDirectory(String applicationName) {
    String s1 = System.getProperty("user.home", ".");
    File file;
    switch (getPlatform().ordinal())
    {
    case 1:
      file = new File(s1, '.' + applicationName + '/');
      break;
    case 2:
      file = new File(System.getenv("APPDATA"), '.' + applicationName + '/');
      break;
    case 3:
      file = new File(s1, '.' + applicationName + '/');
      break;
    case 4:
      file = new File(s1, "Library/Application Support/" + applicationName);
      break;
    default:
      file = new File(s1, '.' + applicationName + '/');
    }

    if ((!file.exists()) && (!file.mkdirs()))
    {
      throw new RuntimeException("The working directory could not be created: " + file);
    }

    return file;
  }

  private static OS getPlatform()
  {
    String osName = System.getProperty("os.name").toLowerCase();
    if (osName.contains("win")) return OS.windows;
    if (osName.contains("mac")) return OS.macos;
    if (osName.contains("solaris")) return OS.solaris;
    if (osName.contains("sunos")) return OS.solaris;
    if (osName.contains("linux")) return OS.linux;
    if (osName.contains("unix")) return OS.linux;
    return OS.unknown;
  }

  private static enum OS
  {
    linux, solaris, windows, macos, unknown;
  }
}

}
