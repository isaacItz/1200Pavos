package vista;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Fondo extends JPanel {
	private ImageIcon imagen;
	private static final long serialVersionUID = 1L;

	public Fondo(String nombre) {
		super();
		inicializate();
		imagen = new ImageIcon(getClass().getResource(nombre));
		setSize(imagen.getIconWidth(), imagen.getIconHeight());
	}

	public void paintComponent(Graphics g) {
		Dimension dimension = getSize();
		g.drawImage(imagen.getImage(), 0, 0, dimension.width, dimension.height, null);
		super.paintComponents(g);
	}

	private void inicializate() {
		setSize(300, 200);
		setLayout(new GridBagLayout());
	}
}
