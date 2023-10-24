package pck;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;

@SuppressWarnings("serial")
public class NuevaVentana extends JFrame{

	public NuevaVentana(DataSetMunicipios dataset, VentanaTablaDatos ventanaTablaDatos) {
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 900, 600 );
		setLocationRelativeTo( null );
		setVisible(true);
		
		JPanel main = new JPanel(new BorderLayout());
		add(main);
		
		//Texto NORTH
		JPanel textoArriba = new JPanel(); //en JPanel para que vaya centrado
		textoArriba.add(new JLabel("Texto Inicialmente Vacio"));
		main.add(textoArriba, BorderLayout.NORTH);
		
		
		
		//Tabla, CENTER
		JTable tabla = new JTable();
		
		JScrollPane tablaScrollPane = new JScrollPane(tabla);
		main.add(tablaScrollPane, BorderLayout.CENTER);
		
		mostrarTablaCompleta(ventanaTablaDatos, dataset, tabla); //hago que la tabla del medio sea la original del principio
		
		
		//Arbol WEST
		JTree arbol = new JTree(dataset.getModeloTree());
		JScrollPane arbolSrollPane = new JScrollPane(arbol);
		main.add(arbolSrollPane, BorderLayout.WEST);
		
		arbol.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) arbol.getLastSelectedPathComponent();
				if(selectedNode != null && selectedNode.isLeaf()) {
					String provSelec = selectedNode.toString();
					cargarMunicipiosconProvincia(provSelec, dataset, tabla);
				}
							
				if(selectedNode.toString().equals("Municipios")) {
					mostrarTablaCompleta(ventanaTablaDatos, dataset, tabla);
				}
				
				
			}
			
		});
	
		
		//Panel, EAST
		JPanel panel = new JPanel();
		panel.setBackground(Color.green);
		main.add(panel, BorderLayout.EAST);
		
		//Botonera, SOUTH
		JPanel botonera = new JPanel();
		JButton insertar = new JButton("Insertar");
		JButton borrar = new JButton("Borrar");
		JButton orden = new JButton("Orden");
		botonera.add(insertar); botonera.add(borrar); botonera.add(orden);
		main.add(botonera, BorderLayout.SOUTH);
	
	}
	
	private void cargarMunicipiosconProvincia(String provinciaSelec, DataSetMunicipios dataset, JTable tabla) {
	    ArrayList<Municipio> muniEnProvincia = new ArrayList<>();
	    for (Municipio muni : dataset.getListaMunicipios()) {
	        if (muni.getProvincia().equals(provinciaSelec)) {
	            muniEnProvincia.add(muni);
	        }
	    }
	    
	    
	    muniEnProvincia.sort(Comparator.comparing(Municipio::getNombre));

	    DefaultTableModel model = new DefaultTableModel();
	    tabla.setBackground(Color.WHITE);
	    model.addColumn("Nombre");
	    model.addColumn("Habitantes");
	    model.addColumn("Provincia");
	    model.addColumn("Autonomía");
	    model.addColumn("Poblacion");



		tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (column == 4 && value instanceof Integer) {
					int habitantes = (Integer) value;
					JProgressBar progressBar = new JProgressBar(50000, 5000000);
					progressBar.setValue(habitantes);

					//cambiar fondo
					float red = 1 - (float) habitantes / 5000000; // Rango de 0 (verde) a 1 (rojo)
					float green = (float) habitantes / 5000000; // Rango de 1 (verde) a 0 (rojo)
					progressBar.setForeground(new Color(red, green, 0));

					return progressBar;
				}
				return c;
			}


		});


		for (Municipio muni : muniEnProvincia) {
	        String nombre = muni.getNombre();
	        int habitantes = muni.getHabitantes();
	        String provincia = muni.getProvincia();
	        String autonomia = muni.getAutonomia();

	        model.addRow(new Object[]{nombre, habitantes, provincia, autonomia, habitantes});
	    }
	    
	    tabla.setModel(model);
	    
		
	}

	private void mostrarTablaCompleta(VentanaTablaDatos ventanaTablaDatos, DataSetMunicipios dataset, JTable tabla){
		ventanaTablaDatos.setDatos(dataset, tabla);
		tabla.setBackground(Color.white);
	}
}
