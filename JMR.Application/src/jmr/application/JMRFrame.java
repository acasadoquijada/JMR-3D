package jmr.application;

import events.PixelEvent;
import events.PixelListener;
import iu.ImageInternalFrame;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import jmr.descriptor.MediaDescriptor;
import jmr.descriptor.mpeg7.MPEG7DominantColors;
import jmr.descriptor.mpeg7.MPEG7DominantColors.MPEG7SingleDominatColor;
import jmr.descriptor.mpeg7.MPEG7ColorStructure;
import jmr.descriptor.mpeg7.MPEG7ScalableColor;
import jmr.initial.media.JMRExtendedBufferedImage;
import jmr.result.FloatResult;
import jmr.result.ResultList;
import jmr.result.ResultMetadata;
import jmr.result.Vector;

/**
 * Ventana principal de la aplicación JMR
 * 
 * @author Jesús Chamorro Martínez (jesus@decsai.ugr.es)
 */
public class JMRFrame extends javax.swing.JFrame {
    
    /*
    * Indica la visualización activa
    *
    */
    
    private int visualizacion = 1;
    
    
    /*
    * Indica el número de desriptores activos
    */
    
    private int des_activos = 1;
    
    
    /**
     * Crea una ventana principal
     */
    
    
    public JMRFrame() {
        initComponents();
        setIconImage((new ImageIcon(getClass().getResource("/icons/iconoJMR.png"))).getImage());
    }
    
    /**
     * Devuelve la ventana interna seleccionada de tipo imagen (null si no hubiese 
     * ninguna selecionada o si fuese de otro tipo) 
     * 
     * @return la ventana interna seleccionada de tipo imagen
     */
    public JMRImageInternalFrame getSelectedImageFrame() {
        JInternalFrame vi = escritorio.getSelectedFrame();
        if(vi instanceof JMRImageInternalFrame)
            return (JMRImageInternalFrame)escritorio.getSelectedFrame();
        else
            return null;
    }
    
    /**
     * Devuelve la imagen de la ventana interna selecionada
     * 
     * @return la imagen seleccionada
     */
    private BufferedImage getSelectedImage(){
        BufferedImage img = null;
        ImageInternalFrame vi = this.getSelectedImageFrame();
        if (vi != null) {
            if (vi.type == ImageInternalFrame.TYPE_STANDAR) {
                img = vi.getImage();
            } 
            else {
                JOptionPane.showInternalMessageDialog(escritorio, "An image must be selected", "Image", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        return img;
    }
        
    /**
     * Devuelve el título de la ventana interna selecionada
     * 
     * @return el título de la ventana interna selecionada
     */
    private String getSelectedFrameTitle(){
        String title = "";
        JInternalFrame vi = escritorio.getSelectedFrame();
        if (vi != null) {
            title = vi.getTitle();
        }
        return title;
    }
    
    /**
     * Sitúa la ventana interna <tt>vi</tt> debajo de la ventana interna activa 
     * y con el mismo tamaño.
     * 
     * @param vi la ventana interna
     */
    private void locateInternalFrame(JInternalFrame vi) {
        JInternalFrame vSel = escritorio.getSelectedFrame();
        if (vSel != null) {
            vi.setLocation(vSel.getX() + 20, vSel.getY() + 20);
            vi.setSize(vSel.getSize());
        }
    }
    
    /**
     * Muestra la ventana interna <tt>vi</tt> 
     * 
     * @param vi la ventana interna
     */
    private void showInternalFrame(JInternalFrame vi) {        
        if(vi instanceof ImageInternalFrame){
            ((ImageInternalFrame)vi).setGrid(this.verGrid.isSelected());
            ((ImageInternalFrame)vi).addPixelListener(new ManejadorPixel());
        }
        this.locateInternalFrame(vi);
        this.escritorio.add(vi);
        vi.setVisible(true);
    }  
    
    /**
     * Clase interna manejadora de eventos de pixel
     */
    private class ManejadorPixel implements PixelListener {
        /**
         * Gestiona el cambio de localización del pixel activo, actualizando
         * la información de la barra de tareas.
         * 
         * @param evt evento de pixel
         */
        @Override
        public void positionChange(PixelEvent evt) {
            String text = " ";
            Point p = evt.getPixelLocation();
            if (p != null) {
                Color c = evt.getRGB();
                Integer alpha = evt.getAlpha();
                text = "(" + p.x + "," + p.y + ") : [" + c.getRed() + "," + c.getGreen() + "," + c.getBlue();
                text += alpha == null ? "]" : ("," + alpha + "]");
            }
            posicionPixel.setText(text);
        }
    } 
    
    /*
    * Activa/desactiva visualizaciones atendiendo 
    * al número de descriptores activos
    */
    
    private void controlVisualizaciones(){
        
        switch(des_activos){
            
            case 0:
                
                jToggleSecuencial.setEnabled(false);
                jToggleSpiral.setEnabled(false);
                jToggleCamino.setEnabled(false);                
                botonCompara.setEnabled(false);
                break;
                
            case 1:
                
                jToggleSecuencial.setEnabled(true);
                jToggleSpiral.setEnabled(true);
                jToggleCamino.setEnabled(true);
                botonCompara.setEnabled(true);
                
                
//                jTogglePol2D.setEnabled(false);
                jToggleNpath.setEnabled(false);
                jToggleCar2D.setEnabled(false);
                break;
                
                
            case 2:
                
  //              jTogglePol2D.setEnabled(true);
                jToggleNpath.setEnabled(true);
                jToggleCar2D.setEnabled(true);
                
//                jTogglePol3D.setEnabled(false);
                jToggleCar3D.setEnabled(false);
                
                break;
                
            case 3:
                jToggleCar3D.setEnabled(true);
//                jTogglePol3D.setEnabled(true);
                break;
            
            
            
        }
        
        
    }
    
    /*
    * Realiza un ajuste lineal x usando alpha
    */
    
    private double ajusteRango(double x, double alpha){
        
        double newX = 0;
        
        
        if(Double.compare(x, 0.0) < 0){
            newX = 0;

        }
        
        else if(0.0 < x && x <= alpha){
            newX=x/alpha;   
                       
        }
        
        if(x > alpha){
            newX = 1.0;
       
        }

      //  System.out.println(x+  " - " + newX);
        return newX;
        
    }
    
    /*
     * Código generado por Netbeans para el diseño del interfaz
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenuPanelOutput = new javax.swing.JPopupMenu();
        clear = new javax.swing.JMenuItem();
        popupMenuSeleccionDescriptores = new javax.swing.JPopupMenu();
        colorDominante = new javax.swing.JRadioButtonMenuItem();
        colorEstructurado = new javax.swing.JRadioButtonMenuItem();
        colorEscalable = new javax.swing.JRadioButtonMenuItem();
        buttonGroupVisualizaciones = new javax.swing.ButtonGroup();
        splitPanelCentral = new javax.swing.JSplitPane();
        escritorio = new javax.swing.JDesktopPane();
        showPanelInfo = new javax.swing.JLabel();
        panelTabuladoInfo = new javax.swing.JTabbedPane();
        panelOutput = new javax.swing.JPanel();
        scrollEditorOutput = new javax.swing.JScrollPane();
        editorOutput = new javax.swing.JEditorPane();
        panelBarraHerramientas = new javax.swing.JPanel();
        barraArchivo = new javax.swing.JToolBar();
        botonAbrir = new javax.swing.JButton();
        botonGuardar = new javax.swing.JButton();
        botonPreferencias = new javax.swing.JButton();
        barraDescriptores = new javax.swing.JToolBar();
        botonDCD = new javax.swing.JButton();
        botonCompara = new javax.swing.JButton();
        barraVisualizaciones = new javax.swing.JToolBar();
        jToggleSecuencial = new javax.swing.JToggleButton();
        jToggleCamino = new javax.swing.JToggleButton();
        jToggleSpiral = new javax.swing.JToggleButton();
        jToggleCar2D = new javax.swing.JToggleButton();
        jToggleCar3D = new javax.swing.JToggleButton();
        jToggleNpath = new javax.swing.JToggleButton();
        barraEstado = new javax.swing.JPanel();
        posicionPixel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        menuAbrir = new javax.swing.JMenuItem();
        menuGuardar = new javax.swing.JMenuItem();
        separador1 = new javax.swing.JPopupMenu.Separator();
        closeAll = new javax.swing.JMenuItem();
        menuVer = new javax.swing.JMenu();
        verGrid = new javax.swing.JCheckBoxMenuItem();
        usarTransparencia = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        showResized = new javax.swing.JCheckBoxMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuZoom = new javax.swing.JMenu();
        menuZoomIn = new javax.swing.JMenuItem();
        menuZoomOut = new javax.swing.JMenuItem();

        popupMenuPanelOutput.setAlignmentY(0.0F);
        popupMenuPanelOutput.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        clear.setText("Clear");
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });
        popupMenuPanelOutput.add(clear);

        colorDominante.setSelected(true);
        colorDominante.setText("Dominant color");
        colorDominante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorDominanteActionPerformed(evt);
            }
        });
        popupMenuSeleccionDescriptores.add(colorDominante);

        colorEstructurado.setText("Structured color");
        colorEstructurado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorEstructuradoActionPerformed(evt);
            }
        });
        popupMenuSeleccionDescriptores.add(colorEstructurado);

        colorEscalable.setText("Scalable color");
        colorEscalable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorEscalableActionPerformed(evt);
            }
        });
        popupMenuSeleccionDescriptores.add(colorEscalable);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Java Multimedia Retrieval");
        setName("ventanaPrincipal"); // NOI18N

        splitPanelCentral.setDividerLocation(1.0);
        splitPanelCentral.setDividerSize(3);
        splitPanelCentral.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPanelCentral.setPreferredSize(new java.awt.Dimension(0, 0));
        splitPanelCentral.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                splitPanelCentralPropertyChange(evt);
            }
        });

        escritorio.setBackground(java.awt.Color.lightGray);
        escritorio.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        showPanelInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/desplegar20.png"))); // NOI18N
        showPanelInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                showPanelInfoMousePressed(evt);
            }
        });

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, escritorioLayout.createSequentialGroup()
                .addGap(0, 929, Short.MAX_VALUE)
                .addComponent(showPanelInfo))
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, escritorioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(showPanelInfo))
        );
        escritorio.setLayer(showPanelInfo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        splitPanelCentral.setTopComponent(escritorio);

        panelTabuladoInfo.setMinimumSize(new java.awt.Dimension(0, 0));
        panelTabuladoInfo.setPreferredSize(new java.awt.Dimension(0, 0));

        panelOutput.setMinimumSize(new java.awt.Dimension(0, 0));
        panelOutput.setPreferredSize(new java.awt.Dimension(0, 0));
        panelOutput.setLayout(new java.awt.BorderLayout());

        scrollEditorOutput.setBorder(null);
        scrollEditorOutput.setMinimumSize(new java.awt.Dimension(0, 0));

        editorOutput.setMinimumSize(new java.awt.Dimension(0, 0));
        editorOutput.setPreferredSize(new java.awt.Dimension(0, 0));
        editorOutput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                editorOutputMouseReleased(evt);
            }
        });
        scrollEditorOutput.setViewportView(editorOutput);

        panelOutput.add(scrollEditorOutput, java.awt.BorderLayout.CENTER);

        panelTabuladoInfo.addTab("Output", panelOutput);

        splitPanelCentral.setBottomComponent(panelTabuladoInfo);

        getContentPane().add(splitPanelCentral, java.awt.BorderLayout.CENTER);

        panelBarraHerramientas.setAlignmentX(0.0F);
        panelBarraHerramientas.setAlignmentY(0.0F);
        panelBarraHerramientas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        barraArchivo.setRollover(true);
        barraArchivo.setAlignmentX(0.0F);

        botonAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open24.png"))); // NOI18N
        botonAbrir.setToolTipText("Open");
        botonAbrir.setFocusable(false);
        botonAbrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonAbrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAbrirActionPerformed(evt);
            }
        });
        barraArchivo.add(botonAbrir);

        botonGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save24.png"))); // NOI18N
        botonGuardar.setToolTipText("Save");
        botonGuardar.setFocusable(false);
        botonGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGuardarActionPerformed(evt);
            }
        });
        barraArchivo.add(botonGuardar);

        botonPreferencias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/settings24.png"))); // NOI18N
        botonPreferencias.setFocusable(false);
        botonPreferencias.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonPreferencias.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonPreferencias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonPreferenciasActionPerformed(evt);
            }
        });
        barraArchivo.add(botonPreferencias);

        panelBarraHerramientas.add(barraArchivo);

        barraDescriptores.setRollover(true);

        botonDCD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/iconDCD24.png"))); // NOI18N
        botonDCD.setToolTipText("DCD");
        botonDCD.setFocusable(false);
        botonDCD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonDCD.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonDCD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonDCDActionPerformed(evt);
            }
        });
        barraDescriptores.add(botonDCD);

        botonCompara.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/compare24.png"))); // NOI18N
        botonCompara.setToolTipText("Compare");
        botonCompara.setComponentPopupMenu(popupMenuSeleccionDescriptores);
        botonCompara.setFocusable(false);
        botonCompara.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        botonCompara.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        botonCompara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonComparaActionPerformed(evt);
            }
        });
        barraDescriptores.add(botonCompara);

        panelBarraHerramientas.add(barraDescriptores);

        barraVisualizaciones.setRollover(true);

        buttonGroupVisualizaciones.add(jToggleSecuencial);
        jToggleSecuencial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/secuencial24.png"))); // NOI18N
        jToggleSecuencial.setSelected(true);
        jToggleSecuencial.setToolTipText("Secuencial ");
        jToggleSecuencial.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jToggleSecuencial.setBorderPainted(false);
        jToggleSecuencial.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jToggleSecuencial.setFocusable(false);
        jToggleSecuencial.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleSecuencial.setMaximumSize(new java.awt.Dimension(31, 31));
        jToggleSecuencial.setMinimumSize(new java.awt.Dimension(31, 31));
        jToggleSecuencial.setPreferredSize(new java.awt.Dimension(31, 31));
        jToggleSecuencial.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleSecuencial.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleSecuencialItemStateChanged(evt);
            }
        });
        barraVisualizaciones.add(jToggleSecuencial);

        buttonGroupVisualizaciones.add(jToggleCamino);
        jToggleCamino.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/path24.png"))); // NOI18N
        jToggleCamino.setToolTipText("Path");
        jToggleCamino.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jToggleCamino.setBorderPainted(false);
        jToggleCamino.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jToggleCamino.setFocusable(false);
        jToggleCamino.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleCamino.setMaximumSize(new java.awt.Dimension(31, 31));
        jToggleCamino.setMinimumSize(new java.awt.Dimension(31, 31));
        jToggleCamino.setPreferredSize(new java.awt.Dimension(31, 31));
        jToggleCamino.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleCamino.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleCaminoItemStateChanged(evt);
            }
        });
        barraVisualizaciones.add(jToggleCamino);

        buttonGroupVisualizaciones.add(jToggleSpiral);
        jToggleSpiral.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/spiral24.png"))); // NOI18N
        jToggleSpiral.setToolTipText("Spiral");
        jToggleSpiral.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jToggleSpiral.setBorderPainted(false);
        jToggleSpiral.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jToggleSpiral.setFocusable(false);
        jToggleSpiral.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleSpiral.setMaximumSize(new java.awt.Dimension(31, 31));
        jToggleSpiral.setMinimumSize(new java.awt.Dimension(31, 31));
        jToggleSpiral.setPreferredSize(new java.awt.Dimension(31, 31));
        jToggleSpiral.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleSpiral.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleSpiralItemStateChanged(evt);
            }
        });
        barraVisualizaciones.add(jToggleSpiral);

        buttonGroupVisualizaciones.add(jToggleCar2D);
        jToggleCar2D.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cartesian2D.png"))); // NOI18N
        jToggleCar2D.setToolTipText("Cartesian 2D");
        jToggleCar2D.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jToggleCar2D.setBorderPainted(false);
        jToggleCar2D.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jToggleCar2D.setEnabled(false);
        jToggleCar2D.setFocusable(false);
        jToggleCar2D.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleCar2D.setMaximumSize(new java.awt.Dimension(31, 31));
        jToggleCar2D.setMinimumSize(new java.awt.Dimension(31, 31));
        jToggleCar2D.setPreferredSize(new java.awt.Dimension(31, 31));
        jToggleCar2D.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleCar2D.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleCar2DItemStateChanged(evt);
            }
        });
        barraVisualizaciones.add(jToggleCar2D);

        buttonGroupVisualizaciones.add(jToggleCar3D);
        jToggleCar3D.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cartesian3D.png"))); // NOI18N
        jToggleCar3D.setToolTipText("Cartesian 3D");
        jToggleCar3D.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jToggleCar3D.setBorderPainted(false);
        jToggleCar3D.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jToggleCar3D.setEnabled(false);
        jToggleCar3D.setFocusable(false);
        jToggleCar3D.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleCar3D.setMaximumSize(new java.awt.Dimension(31, 31));
        jToggleCar3D.setMinimumSize(new java.awt.Dimension(31, 31));
        jToggleCar3D.setPreferredSize(new java.awt.Dimension(31, 31));
        jToggleCar3D.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleCar3D.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleCar3DItemStateChanged(evt);
            }
        });
        barraVisualizaciones.add(jToggleCar3D);

        buttonGroupVisualizaciones.add(jToggleNpath);
        jToggleNpath.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/npath24.png"))); // NOI18N
        jToggleNpath.setToolTipText("N-Path");
        jToggleNpath.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jToggleNpath.setBorderPainted(false);
        jToggleNpath.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jToggleNpath.setEnabled(false);
        jToggleNpath.setFocusable(false);
        jToggleNpath.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleNpath.setMaximumSize(new java.awt.Dimension(31, 31));
        jToggleNpath.setMinimumSize(new java.awt.Dimension(31, 31));
        jToggleNpath.setPreferredSize(new java.awt.Dimension(31, 31));
        jToggleNpath.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleNpath.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jToggleNpathItemStateChanged(evt);
            }
        });
        barraVisualizaciones.add(jToggleNpath);

        panelBarraHerramientas.add(barraVisualizaciones);

        getContentPane().add(panelBarraHerramientas, java.awt.BorderLayout.PAGE_START);

        barraEstado.setLayout(new java.awt.BorderLayout());

        posicionPixel.setText("  ");
        posicionPixel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        barraEstado.add(posicionPixel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(barraEstado, java.awt.BorderLayout.SOUTH);

        menuArchivo.setText("File");

        menuAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open16.png"))); // NOI18N
        menuAbrir.setText("Open");
        menuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAbrirActionPerformed(evt);
            }
        });
        menuArchivo.add(menuAbrir);

        menuGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save16.png"))); // NOI18N
        menuGuardar.setText("Save");
        menuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGuardarActionPerformed(evt);
            }
        });
        menuArchivo.add(menuGuardar);
        menuArchivo.add(separador1);

        closeAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/closeall16.png"))); // NOI18N
        closeAll.setText("Close all");
        closeAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllActionPerformed(evt);
            }
        });
        menuArchivo.add(closeAll);

        menuBar.add(menuArchivo);

        menuVer.setText("View");

        verGrid.setSelected(true);
        verGrid.setText("Show grid");
        verGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verGridActionPerformed(evt);
            }
        });
        menuVer.add(verGrid);

        usarTransparencia.setSelected(true);
        usarTransparencia.setText("Use transparency");
        menuVer.add(usarTransparencia);
        menuVer.add(jSeparator2);

        showResized.setText("Show resized images");
        menuVer.add(showResized);
        menuVer.add(jSeparator1);

        menuZoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/zoom16.png"))); // NOI18N
        menuZoom.setText("Zoom");

        menuZoomIn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PLUS, 0));
        menuZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/zoom-in16.png"))); // NOI18N
        menuZoomIn.setText("Zoom in");
        menuZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuZoomInActionPerformed(evt);
            }
        });
        menuZoom.add(menuZoomIn);

        menuZoomOut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_MINUS, 0));
        menuZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/zoom-out16.png"))); // NOI18N
        menuZoomOut.setText("Zoom out");
        menuZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuZoomOutActionPerformed(evt);
            }
        });
        menuZoom.add(menuZoomOut);

        menuVer.add(menuZoom);

        menuBar.add(menuVer);

        setJMenuBar(menuBar);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void menuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAbrirActionPerformed
        BufferedImage img;
        JFileChooser dlg = new JFileChooser();
        dlg.setMultiSelectionEnabled(true);
        int resp = dlg.showOpenDialog(this);
        if (resp == JFileChooser.APPROVE_OPTION) {
            try {                
                File files[] = dlg.getSelectedFiles();              
                for (File f : files) {
                    img = ImageIO.read(f);
                    if (img != null) {
                        ImageInternalFrame vi = new JMRImageInternalFrame(this, img);
                        vi.setTitle(f.getName());
                        this.showInternalFrame(vi);
                        
                        
                    }
                }               
            } catch (Exception ex) {
                JOptionPane.showInternalMessageDialog(escritorio, "Error in image opening", "Image", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_menuAbrirActionPerformed

    private void menuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGuardarActionPerformed
        BufferedImage img = this.getSelectedImage();
        if (img != null) {
            JFileChooser dlg = new JFileChooser();
            int resp = dlg.showSaveDialog(this);
            if (resp == JFileChooser.APPROVE_OPTION) {
                File f = dlg.getSelectedFile();
                try {
                    ImageIO.write(img, "png", f);
                    escritorio.getSelectedFrame().setTitle(f.getName());
                } catch (Exception ex) {
                    JOptionPane.showInternalMessageDialog(escritorio, "Error in image saving", "Image", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }      
    }//GEN-LAST:event_menuGuardarActionPerformed

    private void botonAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAbrirActionPerformed
        this.menuAbrirActionPerformed(evt);
    }//GEN-LAST:event_botonAbrirActionPerformed

    private void botonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGuardarActionPerformed
        this.menuGuardarActionPerformed(evt);
    }//GEN-LAST:event_botonGuardarActionPerformed
    
    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        this.editorOutput.setText("");
    }//GEN-LAST:event_clearActionPerformed

    private void splitPanelCentralPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_splitPanelCentralPropertyChange
        if (evt.getPropertyName().equals("dividerLocation")) {
            float dividerLocation = (float) splitPanelCentral.getDividerLocation() / splitPanelCentral.getMaximumDividerLocation();
            if (dividerLocation >= 1) {//Está colapsada
                showPanelInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/desplegar20.png")));
            } else {
                showPanelInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cerrar16.png")));
            }
        }
    }//GEN-LAST:event_splitPanelCentralPropertyChange

    private void editorOutputMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editorOutputMouseReleased
        if(evt.isPopupTrigger()){
            Point p = this.scrollEditorOutput.getMousePosition();
            this.popupMenuPanelOutput.show(this.panelOutput,p.x,p.y);
        }
    }//GEN-LAST:event_editorOutputMouseReleased

    private void showPanelInfoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_showPanelInfoMousePressed
        float dividerLocation = (float)splitPanelCentral.getDividerLocation()/splitPanelCentral.getMaximumDividerLocation();
        if(dividerLocation>=1) {//Está colapsada
            splitPanelCentral.setDividerLocation(0.8);
        } else{
            splitPanelCentral.setDividerLocation(1.0);
        }
    }//GEN-LAST:event_showPanelInfoMousePressed

    private void closeAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAllActionPerformed
       escritorio.removeAll();
       escritorio.repaint();
    }//GEN-LAST:event_closeAllActionPerformed

    private void botonPreferenciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonPreferenciasActionPerformed
        
    }//GEN-LAST:event_botonPreferenciasActionPerformed

    private void menuZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuZoomOutActionPerformed
        ImageInternalFrame vi = this.getSelectedImageFrame();
        if (vi != null) {
            int zoom = vi.getZoom();
            if(zoom>=2){
                vi.setZoom(zoom-1);
                vi.repaint();
            }
        }
    }//GEN-LAST:event_menuZoomOutActionPerformed

    private void menuZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuZoomInActionPerformed
        ImageInternalFrame vi = this.getSelectedImageFrame();
        if (vi != null) {
            vi.setZoom(vi.getZoom()+1);
            vi.repaint();
        }
    }//GEN-LAST:event_menuZoomInActionPerformed

    private void verGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verGridActionPerformed
        JInternalFrame ventanas[] = escritorio.getAllFrames();
        for(JInternalFrame vi: ventanas){
            ((ImageInternalFrame)vi).setGrid(this.verGrid.isSelected());
            vi.repaint();
        }
    }//GEN-LAST:event_verGridActionPerformed

    private void botonDCDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonDCDActionPerformed
        BufferedImage img = this.getSelectedImage();
        if (img != null) {  
            java.awt.Cursor cursor = this.getCursor();
            setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            MPEG7DominantColors dCDC = new MPEG7DominantColors();
            BufferedImage imgResized = dCDC.calculate(img,true); //Calculamos el desciptor
            setCursor(cursor);
            //Mostramos resultado
            JMRImageInternalFrame vi = this.getSelectedImageFrame();
            vi.setDominantColorDescriptor(dCDC);
            String text = editorOutput.getText();
            text += "DCD ("+this.getSelectedFrameTitle()+")\n";
            int colorIndex = 1;
            for(MPEG7SingleDominatColor c : dCDC.getDominantColors()){
                text += "   Color "+(colorIndex++)+": [" + c.getColor().getRed() + "," + c.getColor().getGreen() + "," + c.getColor().getBlue()+"]\n";
            }
            this.editorOutput.setText(text);            
            if(this.showResized.isSelected()){
                JMRImageInternalFrame vi_resized = new JMRImageInternalFrame(null, imgResized);
                vi_resized.setTitle(this.getSelectedFrameTitle()+" resized");
                this.showInternalFrame(vi_resized);     
            }
        }
    }//GEN-LAST:event_botonDCDActionPerformed

    private void botonComparaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonComparaActionPerformed
        JMRImageInternalFrame viAnalyzed, viQuery = this.getSelectedImageFrame();
        if (viQuery != null) {
            java.awt.Cursor cursor = this.getCursor();
            setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
                        
            //Calculamos descriptores en la imagen consulta
            ArrayList<MediaDescriptor> descriptores_query = new ArrayList();
            if (this.colorDominante.isSelected()) {
                MPEG7DominantColors dcd_query = viQuery.getDominantColorDescriptor();
                if (dcd_query == null) {
                    dcd_query = new MPEG7DominantColors();
                    dcd_query.calculate(this.getSelectedImage(), true);
                    viQuery.setDominantColorDescriptor(dcd_query);
                }
                descriptores_query.add(dcd_query);                
            }
            if (this.colorEstructurado.isSelected()) {
                MPEG7ColorStructure dcs_query = new MPEG7ColorStructure();
                JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(this.getSelectedImage());
                dcs_query.calculate(imgJMR);
                descriptores_query.add(dcs_query);
            }
            
            if(this.colorEscalable.isSelected()){
                MPEG7ScalableColor dsc_query = new MPEG7ScalableColor();
                JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(this.getSelectedImage());
                dsc_query.calculate(imgJMR);
                descriptores_query.add(dsc_query);
            }
            
            //Comparamos la imagen consulta con el resto de imágenes del escritorio                        
            Vector vresult;
            ResultList resultList = new ResultList();
            String text = editorOutput.getText();
            JInternalFrame ventanas[] = escritorio.getAllFrames();           
            for (JInternalFrame vi : ventanas) {                
                if (vi instanceof JMRImageInternalFrame) {
                    viAnalyzed = (JMRImageInternalFrame) vi;

                    Iterator<MediaDescriptor> itQuery = descriptores_query.iterator();
                    MediaDescriptor current_descriptor;
                    vresult = new Vector(descriptores_query.size());
                    int index = 0;

                    //DCD
                    if (this.colorDominante.isSelected()) {
                        MPEG7DominantColors dcd_analyzed = viAnalyzed.getDominantColorDescriptor();
                        if (dcd_analyzed == null) {
                            dcd_analyzed = new MPEG7DominantColors();
                            dcd_analyzed.calculate(viAnalyzed.getImage(), true);
                            viAnalyzed.setDominantColorDescriptor(dcd_analyzed);
                        }
                        current_descriptor = itQuery.next();
                        FloatResult result = (FloatResult) current_descriptor.compare(dcd_analyzed);
                        vresult.setCoordinate(index++, result.toDouble());
                    }
                    //CSD
                    if (this.colorEstructurado.isSelected()) {
                        MPEG7ColorStructure dcs_analyzed = new MPEG7ColorStructure();
                        JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(viAnalyzed.getImage());
                        dcs_analyzed.calculate(imgJMR);
                        current_descriptor = itQuery.next();
                        
                        FloatResult result = (FloatResult) current_descriptor.compare(dcs_analyzed);
                        double result2 = ajusteRango(result.toDouble(), 0.1);
                        vresult.setCoordinate(index++, result2);
                    }
                    
                    //Escalable
                    if(this.colorEscalable.isSelected()){
                        MPEG7ScalableColor dsc_analyzed = new MPEG7ScalableColor();
                        JMRExtendedBufferedImage imgJMR = new JMRExtendedBufferedImage(viAnalyzed.getImage());
                        dsc_analyzed.calculate(imgJMR);
                        current_descriptor = itQuery.next();
                        FloatResult result = (FloatResult) current_descriptor.compare(dsc_analyzed);
                        double result2 = ajusteRango(result.toDouble(), 820);
                        vresult.setCoordinate(index++, result2);    
                    }
                    
                    resultList.add(new ResultMetadata(vresult, viAnalyzed.getImage()));
                    text += "Dist("+viQuery.getTitle()+","+viAnalyzed.getTitle()+") = ";
                    text += vresult!=null ? vresult.toString()+"\n" : "No calculado\n"; 
                }
            }            
            this.editorOutput.setText(text); 
            setCursor(cursor);     
            //Creamas la ventana interna con los resultados
            resultList.sort();

            
            switch(visualizacion){
                
                case 1:
                   /* SecuencialInternalFrame secFrame = new SecuencialInternalFrame(resultList);
                    this.escritorio.add(secFrame);
                    secFrame.setVisible(true);  
                    */
                    
                    ImageListInternalFrame im = new ImageListInternalFrame(resultList);
                    this.escritorio.add(im);
                    im.setVisible(true);
                    break;
                    
                case 2:
                    SpiralInternalFrame spiFrame = new SpiralInternalFrame(resultList);
                    this.escritorio.add(spiFrame);
                    spiFrame.setVisible(true);
                    
                    break;
                    
                case 3:
                    PathInternalFrame pathFrame = new PathInternalFrame(resultList);
                    this.escritorio.add(pathFrame);
                    pathFrame.setVisible(true);
                    break;
                                   
                case 4:
                    Cartesian2DJInternalFrame car2Frame = 
                            new Cartesian2DJInternalFrame(resultList);

                    this.escritorio.add(car2Frame);
                    car2Frame.setVisible(true);
                    break;
                    
                    
                case 5:
                    Polar2DInternalFrame pol2Frame = new Polar2DInternalFrame(resultList);
                    this.escritorio.add(pol2Frame);
                    pol2Frame.setVisible(true);
                  
                    break;

                                        
                case 6:
                    Cartesian3DInternalFrame car3Frame = 
                            new Cartesian3DInternalFrame(resultList);
                    this.escritorio.add(car3Frame);
                    car3Frame.setVisible(true);
                    break;
                    
                case 7:
                    Polar3DInternalFrame pol3Frame = new Polar3DInternalFrame(resultList);
                    this.escritorio.add(pol3Frame);
                    pol3Frame.setVisible(true);
                  
                    break;        
                    
                case 8:
                    NPathInternalFrame arcFrame = new NPathInternalFrame(resultList);
                    this.escritorio.add(arcFrame);
                    arcFrame.setVisible(true);
                    
                    break;
                    
                    


            }
           // SecuencialInternalFrame secFrame = new SecuencialInternalFrame(resultList);
           // ImageListInternalFrame listFrame = new ImageListInternalFrame(resultList);
           // SpiralInternalFrame spiFrame = new SpiralInternalFrame(resultList);
            //this.escritorio.add(secFrame);
            //secFrame.setVisible(true);                
        }
    }//GEN-LAST:event_botonComparaActionPerformed

    private void jToggleSecuencialItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jToggleSecuencialItemStateChanged

        if(jToggleSecuencial.isSelected()){
            visualizacion=1;
        }
    }//GEN-LAST:event_jToggleSecuencialItemStateChanged

    private void jToggleSpiralItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jToggleSpiralItemStateChanged
        
        if(jToggleSpiral.isSelected()){
            visualizacion=2;
        }
    }//GEN-LAST:event_jToggleSpiralItemStateChanged

    private void jToggleCaminoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jToggleCaminoItemStateChanged

        if(jToggleCamino.isSelected()){
            visualizacion=3;
        }
    }//GEN-LAST:event_jToggleCaminoItemStateChanged

    private void colorDominanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorDominanteActionPerformed

        if(this.colorDominante.isSelected()){
            des_activos++;
        }
        
        else{
            des_activos--;
            
        }
        
        controlVisualizaciones();
        
    }//GEN-LAST:event_colorDominanteActionPerformed

    private void colorEstructuradoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorEstructuradoActionPerformed
        if(colorEstructurado.isSelected()){
            des_activos++;
        }
        
        else{
            
            des_activos--;
            
        }
        
        controlVisualizaciones();
            
    }//GEN-LAST:event_colorEstructuradoActionPerformed

    private void jToggleNpathItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jToggleNpathItemStateChanged

        if(jToggleNpath.isSelected()){
            visualizacion = 8;
        }


    }//GEN-LAST:event_jToggleNpathItemStateChanged

    private void jToggleCar2DItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jToggleCar2DItemStateChanged

        if(jToggleCar2D.isSelected()){
            visualizacion = 4;
        }

    }//GEN-LAST:event_jToggleCar2DItemStateChanged

    private void jToggleCar3DItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jToggleCar3DItemStateChanged

        if(jToggleCar3D.isSelected()){
            visualizacion = 6;
        }

    }//GEN-LAST:event_jToggleCar3DItemStateChanged

    private void colorEscalableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorEscalableActionPerformed
       
        if(this.colorEscalable.isSelected()){
            des_activos++;
        }
        
        else{
            
            des_activos--;
            
        }

        controlVisualizaciones();
    }//GEN-LAST:event_colorEscalableActionPerformed
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar barraArchivo;
    private javax.swing.JToolBar barraDescriptores;
    private javax.swing.JPanel barraEstado;
    private javax.swing.JToolBar barraVisualizaciones;
    private javax.swing.JButton botonAbrir;
    private javax.swing.JButton botonCompara;
    private javax.swing.JButton botonDCD;
    private javax.swing.JButton botonGuardar;
    private javax.swing.JButton botonPreferencias;
    private javax.swing.ButtonGroup buttonGroupVisualizaciones;
    private javax.swing.JMenuItem clear;
    private javax.swing.JMenuItem closeAll;
    private javax.swing.JRadioButtonMenuItem colorDominante;
    private javax.swing.JRadioButtonMenuItem colorEscalable;
    private javax.swing.JRadioButtonMenuItem colorEstructurado;
    private javax.swing.JEditorPane editorOutput;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToggleButton jToggleCamino;
    private javax.swing.JToggleButton jToggleCar2D;
    private javax.swing.JToggleButton jToggleCar3D;
    private javax.swing.JToggleButton jToggleNpath;
    private javax.swing.JToggleButton jToggleSecuencial;
    private javax.swing.JToggleButton jToggleSpiral;
    private javax.swing.JMenuItem menuAbrir;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuGuardar;
    public javax.swing.JMenu menuVer;
    private javax.swing.JMenu menuZoom;
    private javax.swing.JMenuItem menuZoomIn;
    private javax.swing.JMenuItem menuZoomOut;
    private javax.swing.JPanel panelBarraHerramientas;
    private javax.swing.JPanel panelOutput;
    private javax.swing.JTabbedPane panelTabuladoInfo;
    private javax.swing.JPopupMenu popupMenuPanelOutput;
    private javax.swing.JPopupMenu popupMenuSeleccionDescriptores;
    public javax.swing.JLabel posicionPixel;
    private javax.swing.JScrollPane scrollEditorOutput;
    private javax.swing.JPopupMenu.Separator separador1;
    private javax.swing.JLabel showPanelInfo;
    private javax.swing.JCheckBoxMenuItem showResized;
    public javax.swing.JSplitPane splitPanelCentral;
    private javax.swing.JCheckBoxMenuItem usarTransparencia;
    private javax.swing.JCheckBoxMenuItem verGrid;
    // End of variables declaration//GEN-END:variables

}
