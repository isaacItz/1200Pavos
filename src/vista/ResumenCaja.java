package vista;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import modelo.Conexion;
import modelo.Utileria;

public class ResumenCaja extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private Conexion conexion;
	private JLabel inicio;
	private JLabel ultimoDeposito;
	private JLabel total;
	private JLabel ultimoCorte;
	private JLabel depositado;

	public ResumenCaja(Conexion conexion) {
		setResizable(false);
		setTitle("Resumen de Caja");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.conexion = conexion;
		setModal(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(ResumenCaja.class.getResource("/vista/logo.png")));
		setSize(422, 285);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Resumen Caja", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(16, 16, 390, 207);
		contentPanel.add(panel);
		panel.setLayout(null);

		JLabel lblInicio = new JLabel("Inicio:");
		lblInicio.setBounds(16, 27, 46, 14);
		panel.add(lblInicio);

		JLabel lblTotalEnCaja = new JLabel("Total en Caja:");
		lblTotalEnCaja.setBounds(16, 58, 87, 14);
		panel.add(lblTotalEnCaja);

		JLabel lblFechaDeUltimo = new JLabel("Fecha de Ultimo Deposito:");
		lblFechaDeUltimo.setBounds(16, 162, 151, 14);
		panel.add(lblFechaDeUltimo);

		JLabel lblFechaDeUltimo_1 = new JLabel("Fecha de Ultimo Corte:");
		lblFechaDeUltimo_1.setBounds(16, 126, 151, 14);
		panel.add(lblFechaDeUltimo_1);

		inicio = new JLabel("0.00");
		inicio.setBounds(207, 27, 104, 14);
		panel.add(inicio);

		total = new JLabel("0.00");
		total.setBounds(207, 58, 104, 14);
		panel.add(total);

		ultimoDeposito = new JLabel("0.00");
		ultimoDeposito.setBounds(207, 162, 173, 14);
		panel.add(ultimoDeposito);

		ultimoCorte = new JLabel("0.00");
		ultimoCorte.setBounds(206, 126, 184, 14);
		panel.add(ultimoCorte);

		JLabel lblDepositado = new JLabel("Depositado:");
		lblDepositado.setBounds(16, 94, 87, 14);
		panel.add(lblDepositado);

		depositado = new JLabel("0.00");
		depositado.setBounds(207, 94, 104, 14);
		panel.add(depositado);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BorderLayout(0, 0));
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				getRootPane().setDefaultButton(okButton);
			}
		}
		setDatos();
	}

	private void setDatos() {
		try {

			String consulta = "Select max(id_cambio) from caja";
			ResultSet rs = conexion.Consulta(consulta);
			int maximo;
			if (rs.next()) {
				maximo = rs.getInt(1);
				consulta = "SELECT * FROM caja where id_cambio = " + maximo;
				rs = conexion.Consulta(consulta);
				rs.next();
				inicio.setText(rs.getString(2));
				total.setText(rs.getString(3));
				ultimoCorte.setText(rs.getString(5));
				depositado.setText(rs.getString(6));
				ultimoDeposito.setText(rs.getString(4));

			} else
				Utileria.escribir("No Se Ha Ingresado Dinero A La Caja");

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}
}
