/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmr.application;

import jmr.iu.SecuencialPanel;
import jmr.result.ResultList;

/**
 *
 * @author alejandro
 */
public class SecuencialInternalFrame extends javax.swing.JInternalFrame {

    /**
     * Creates new form SecuencialInternalFrame
     */
    private SecuencialPanel secuencialpanel;
    
    public SecuencialInternalFrame() {
        initComponents();
        
        secuencialpanel = new SecuencialPanel();
        
        getContentPane().add(secuencialpanel, java.awt.BorderLayout.CENTER);

    }
    
    public SecuencialInternalFrame(ResultList list){
        this();
        if(list!= null){
            secuencialpanel.add(list);
            System.setProperty("sun.awt.noerasebackground", "true");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Secuencial visualization");
        setMinimumSize(new java.awt.Dimension(500, 500));
        setPreferredSize(new java.awt.Dimension(500, 500));

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
