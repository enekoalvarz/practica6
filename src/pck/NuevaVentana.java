package pck;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;

@SuppressWarnings("serial")
public class NuevaVentana extends JFrame{

	private int elementosBorrados = 1;
	protected int poblacionMunicipioSeleccionado = -1;
	protected JTable tabla;
	protected int poblacionCelda;

	private boolean primerClick;

	public NuevaVentana(DataSetMunicipios dataset, VentanaTablaDatos ventanaTablaDatos) {
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setSize( 900, 600 );
		setLocationRelativeTo( null );
		setVisible(true);

		JPanel main = new JPanel(new BorderLayout());
		add(main);
		
		//Texto NORTH
		JPanel textoArriba = new JPanel(); //en JPanel para que vaya centrado
		textoArriba.add(new JLabel("LOS MUNICIPIOS DE ESPAÑA SI"));
		main.add(textoArriba, BorderLayout.NORTH);
		
		
		
		//Tabla, CENTER
		tabla = new JTable();

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

				/*
				if(selectedNode.toString().equals("Municipios")) {
					mostrarTablaCompleta(ventanaTablaDatos, dataset, tabla);
				}
				 */
				
			}
			
		});
	
		
		//Panel, EAST
		JPanel panel = new JPanel();
		//panel.setBackground(Color.green);
		main.add(panel, BorderLayout.EAST);
		
		//Botonera, SOUTH
		JPanel botonera = new JPanel();
		JButton insertar = new JButton("Insertar");
		JButton borrar = new JButton("Borrar");
		JButton orden = new JButton("Orden");
		botonera.add(insertar); botonera.add(borrar); botonera.add(orden);
		main.add(botonera, BorderLayout.SOUTH);


		//FUNCIONA EN LAS DOS TABLAS PERO EN PROVINCIAS HAY QUE CAMBIAR DE PROVINCIA Y VOLVER PARA VER CAMBIOS
		insertar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//System.out.println(dataset.getListaMunicipios());
				int codigo = dataset.getListaMunicipios().size()+1;
				String nombre= "";
				int habitantes = 500000;
				String provincia = (String) tabla.getValueAt(tabla.getSelectedRow(), 3);
				String autonomia = (String) tabla.getValueAt(tabla.getSelectedRow(), 4);
				int ingresos = 0;
				double paro = 0;

				Municipio nuevo = new Municipio(codigo, nombre, habitantes, provincia, autonomia, ingresos, paro);
				dataset.getListaMunicipios().add(nuevo);
				System.out.println(dataset.getListaMunicipios());
			}
		});

		borrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int index = -1;
				for(Municipio muni : dataset.getListaMunicipios()){
					index++;
					if(muni.getCodigo() == (int)tabla.getValueAt(tabla.getSelectedRow(), 0)){
						dataset.getListaMunicipios().remove(index);
					}
				}


			}
		});


		orden.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		tabla.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int clickEnColumna = tabla.columnAtPoint(e.getPoint());
				if (e.getButton() == MouseEvent.BUTTON3 && clickEnColumna==1){
					if(poblacionMunicipioSeleccionado ==-1){
						int clickEnFila = tabla.rowAtPoint(e.getPoint());
						poblacionMunicipioSeleccionado = (int) tabla.getValueAt(clickEnFila,2);
					}else{
						poblacionMunicipioSeleccionado = -1;
					}
				}
			}
		});


		//RENDERER PARA LA TABLA INICIAL CON TODO MEZCLADO
		tabla.setDefaultRenderer( String.class, new DefaultTableCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				c.setBackground(Color.WHITE);
				if(column == 1) {
					poblacionCelda = (int) tabla.getValueAt(row, 2);
					System.out.println(poblacionCelda);
					System.out.println(poblacionMunicipioSeleccionado);
					if(poblacionMunicipioSeleccionado == -1 || poblacionCelda==poblacionMunicipioSeleccionado){
						c.setBackground(Color.WHITE);
					}else if(poblacionCelda > poblacionMunicipioSeleccionado){
						System.out.println(poblacionCelda);
						c.setBackground(Color.RED);
					}else if(poblacionCelda < poblacionMunicipioSeleccionado){
						c.setBackground(Color.green);
					}
				}

				return c;
			}
		});
	
	}


	private void cargarMunicipiosconProvincia(String provinciaSelec, DataSetMunicipios dataset, JTable tabla) {
	    ArrayList<Municipio> muniEnProvincia = new ArrayList<>();

	    for (Municipio muni : dataset.getListaMunicipios()) {
	        if (muni.getProvincia().equals(provinciaSelec)) {
	            muniEnProvincia.add(muni);
	        }
	    }
	    
	    
	    muniEnProvincia.sort(Comparator.comparing(Municipio::getNombre));

	    DefaultTableModel model2 = new DefaultTableModel();

		model2.addColumn("Código");
	    model2.addColumn("Nombre");
	    model2.addColumn("Habitantes");
	    model2.addColumn("Provincia");
	    model2.addColumn("Autonomía");
		model2.addColumn("Ingresos Medios");
		model2.addColumn("Tasa de Paro");
		model2.addColumn("Poblacion"); //aqui progressbar (num 6)


		for (Municipio muni : muniEnProvincia) {
			int codigo = muni.getCodigo();
	        String nombre = muni.getNombre();
	        int habitantes = muni.getHabitantes();
	        String provincia = muni.getProvincia();
	        String autonomia = muni.getAutonomia();
			int ingresos = muni.getIngresos();
			double paro = muni.getParo();

	        model2.addRow(new Object[]{codigo,nombre, habitantes, provincia, autonomia, ingresos, paro, habitantes});
	    }

		//RENDERER DE LA TABLA DE PROVINCIAS
		tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (column == 7 && value instanceof Integer) {
					int habitantes = (Integer) value;
					JProgressBar progressBar = new JProgressBar(50000, 5000000);
					progressBar.setValue(habitantes);

					//cambiar fondo
					float green = 1 - (float) habitantes / 5000000; // Rango de 0 (verde) a 1 (rojo)
					float red = (float) habitantes / 5000000; // Rango de 1 (verde) a 0 (rojo)
					progressBar.setForeground(new Color(red, green, 0));

					return progressBar;
				}

				if(column == 1) {
					int poblacionCelda = (int) tabla.getValueAt(row, 2);
					System.out.println(poblacionCelda);
					System.out.println(poblacionMunicipioSeleccionado);
					if(poblacionMunicipioSeleccionado == -1 || poblacionCelda==poblacionMunicipioSeleccionado){
						c.setBackground(Color.WHITE);
					}else if(poblacionCelda > poblacionMunicipioSeleccionado){
						System.out.println(poblacionCelda);
						c.setBackground(Color.RED);
					}else if(poblacionCelda < poblacionMunicipioSeleccionado){
						c.setBackground(Color.green);
					}
				}


				return c;
			}


		});

	    tabla.setModel(model2);
	    
		
	}

	private void mostrarTablaCompleta(VentanaTablaDatos ventanaTablaDatos, DataSetMunicipios dataset, JTable tabla){
		ventanaTablaDatos.setDatos(dataset, tabla);
		tabla.setBackground(Color.white);
	}
}
