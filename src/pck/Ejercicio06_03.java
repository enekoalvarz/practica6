package pck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Ejercicio06_03 {
	
	private static JFrame ventana;
	private static DataSetMunicipios dataset;

	private static VentanaTablaDatos ventanaDatos;
	
	public static void main(String[] args) {
		/* TODO AUTOMATIZADO.-
		ventana = new JFrame( "Ejercicio 6.3" );
		ventana.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		ventana.setLocationRelativeTo( null );
		ventana.setSize( 200, 80 );

		JButton bCargaMunicipios = new JButton( "Carga municipios > 200k" );
		ventana.add( bCargaMunicipios );
		
		bCargaMunicipios.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cargaMunicipios();
				
			}
		});
		
		
		ventana.setVisible( true );
		*/
		
		cargaMunicipios();
	}
	
	private static void cargaMunicipios() {
		try {
			dataset = new DataSetMunicipios( "municipios_nuevos.txt" );
			/*
			System.out.println( "Cargados municipios:" );
			for (Municipio m : dataset.getListaMunicipios() ) {
				System.out.println( "\t" + m );
			}
			*/
			
			// TODO Resolver el ejercicio 6.3

			ventanaDatos = new VentanaTablaDatos( ventana );

			NuevaVentana v= new NuevaVentana(dataset, ventanaDatos);
			v.setVisible(true);
			/*
			ventanaDatos.setDatos( dataset);
			ventanaDatos.setVisible( true );
			^*/


		} catch (IOException e) {
			System.err.println( "Error en carga de municipios" );
		}
	}
	
}
