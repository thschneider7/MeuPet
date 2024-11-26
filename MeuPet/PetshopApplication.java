package com.example.petshop;

import javax.swing.*;
import java.awt.*;

public class PetshopApplication {
    public static void start() {

        JFrame frame = new JFrame("Petshop");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JButton clienteButton = new JButton("Cadastrar Cliente");
        clienteButton.addActionListener(e -> new PetShopApp().setVisible(true));
        clienteButton.setBackground(Color.GRAY);
        clienteButton.setForeground(Color.WHITE);

        JButton petButton = new JButton("Cadastrar Pet");
        petButton.addActionListener(e -> new PetForm().setVisible(true));
        petButton.setBackground(Color.GRAY);
        petButton.setForeground(Color.WHITE);

        JButton vacinaButton = new JButton("Cadastrar Vacina");
        vacinaButton.addActionListener(e -> new VacinaForm().setVisible(true));
        vacinaButton.setBackground(Color.GRAY);
        vacinaButton.setForeground(Color.WHITE);

        JButton listarDadosButton = new JButton("Listar Dados");
        listarDadosButton.addActionListener(e -> new PetShopApp().setVisible(true));
        listarDadosButton.setBackground(Color.GRAY);
        listarDadosButton.setForeground(Color.WHITE);

        JButton atualizarDadosButton = new JButton("Atualizar Dados");
        atualizarDadosButton.addActionListener(e -> new AtualizarDadosForm().setVisible(true));
        atualizarDadosButton.setBackground(Color.GRAY);
        atualizarDadosButton.setForeground(Color.WHITE);

        JButton dadosPetButton = new JButton("Histórico dos Pets");
        dadosPetButton.addActionListener(e -> new PetShopApp().setVisible(true));
        dadosPetButton.setBackground(Color.GRAY);
        dadosPetButton.setForeground(Color.WHITE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));
        panel.add(clienteButton);
        panel.add(petButton);
        panel.add(vacinaButton);
        panel.add(listarDadosButton);
        panel.add(atualizarDadosButton);
        panel.add(dadosPetButton);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Database.initialize();
            start();
        });
    }
}
