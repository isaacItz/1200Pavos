package modelo;

import java.util.Calendar;

import javax.swing.JOptionPane;

public class Utileria {
	public static boolean continuar(String mensaje) {
		int opcion = JOptionPane.showConfirmDialog(null, mensaje, null, JOptionPane.YES_NO_OPTION);
		if (opcion == JOptionPane.YES_OPTION)
			return true;
		else
			return false;
	}

	public static void escribir(Object mensaje, int tipo) {
		JOptionPane.showMessageDialog(null, mensaje, null, tipo);
	}

	public static int leerInt(String mensaje) {
		do {
			String entrada = leerCadena(mensaje);
			try {
				int numero = Integer.parseInt(entrada);
				return numero;
			} catch (NumberFormatException nfe) {
				escribir("invalido");
			}
		} while (true);
	}

	public static double leerDouble(String mensaje) {
		String entrada = leerCadena(mensaje);
		double numero = Double.parseDouble(entrada);
		return numero;
	}

	public static String obtenerTimesStamp() {
		java.util.Date utilDate = new java.util.Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(utilDate);
		cal.set(Calendar.MILLISECOND, 0);
		System.out.println(new java.sql.Timestamp(cal.getTimeInMillis()));
		return new java.sql.Timestamp(utilDate.getTime()).toString();
	}

	public static String leerCadena(String mensaje) {
		String cadena = JOptionPane.showInputDialog(mensaje);
		return cadena;
	}

	public static boolean valido(String a) {
		return !a.isEmpty() ? true : false;
	}

	public static void escribir(Object msj) {
		try {
			JOptionPane.showMessageDialog(null, msj);
		} catch (Exception e) {
			System.err.println("cancelar");
		}
	}

	public static boolean esNumeroDecimal(String cadena) {
		try {
			Double.parseDouble(cadena);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static boolean esNumero(String cadena) {
		try {
			Integer.parseInt(cadena);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static int mostrarMenu(Object[] opciones) {
		String menuprincipal = "Menu Principal\n";
		int i;
		for (i = 0; i < opciones.length; i++) {
			menuprincipal = menuprincipal.concat(i + 1 + " " + opciones[i] + "\n");
		}
		menuprincipal = menuprincipal + (++i) + " Salir";
		menuprincipal += "\nSeleccione su Opcion";
		int op = 0;
		do {
			op = leerInt(menuprincipal);
		} while (op < 1 || op > opciones.length + 1);
		return op;
	}
}
