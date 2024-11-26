package com.example.petshop;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PetShopApp extends JFrame {
    private JTextField nomeField;
    private JComboBox<String> sexoField;
    private JTextField cpfField;
    private JTextField emailField;
    private JTextField celularField;
    private JTextField enderecoField;
    private JComboBox<String> petComboBox;
    private JTextField pesoField;
    private JTextField alturaField;
    private JTable historicoTable;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private PetShopDAO dao;

    public PetShopApp() {
        dao = new PetShopDAO();
        setTitle("Petshop");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

       
        JPanel clientePanel = new JPanel(new GridLayout(7, 2));
        nomeField = new JTextField();
        sexoField = new JComboBox<>(new String[]{"M", "F"});
        cpfField = new JTextField();
        emailField = new JTextField();
        celularField = new JTextField();
        enderecoField = new JTextField();
        JButton saveButton = new JButton("Salvar");
        saveButton.addActionListener(e -> saveCliente());

        clientePanel.add(new JLabel("Nome:"));
        clientePanel.add(nomeField);
        clientePanel.add(new JLabel("Sexo (M/F):"));
        clientePanel.add(sexoField);
        clientePanel.add(new JLabel("CPF:"));
        clientePanel.add(cpfField);
        clientePanel.add(new JLabel("E-mail:"));
        clientePanel.add(emailField);
        clientePanel.add(new JLabel("Celular:"));
        clientePanel.add(celularField);
        clientePanel.add(new JLabel("Endereço:"));
        clientePanel.add(enderecoField);
        clientePanel.add(saveButton);

        tabbedPane.addTab("Cadastrar Cliente", clientePanel);

    
        JPanel dadosPetPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        petComboBox = new JComboBox<>();
        pesoField = new JTextField();
        alturaField = new JTextField();
        JButton adicionarButton = new JButton("Adicionar Dados");
        adicionarButton.addActionListener(e -> adicionarDados());

        formPanel.add(new JLabel("Selecione o Pet:"));
        formPanel.add(petComboBox);
        formPanel.add(new JLabel("Peso:"));
        formPanel.add(pesoField);
        formPanel.add(new JLabel("Altura:"));
        formPanel.add(alturaField);
        formPanel.add(adicionarButton);

        historicoTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(historicoTable);
        dadosPetPanel.add(formPanel, BorderLayout.NORTH);
        dadosPetPanel.add(scrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Histórico dos Pets", dadosPetPanel);

        
        JPanel listarDadosPanel = new JPanel(new BorderLayout());
        tree = new JTree();
        JScrollPane treeScrollPane = new JScrollPane(tree);
        listarDadosPanel.add(treeScrollPane, BorderLayout.CENTER);

        tabbedPane.addTab("Listar Dados", listarDadosPanel);

        add(tabbedPane);

        carregarPets();
        loadTreeData();
    }

    private void saveCliente() {
        if (nomeField.getText().isEmpty() ||
            cpfField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            celularField.getText().isEmpty() ||
            enderecoField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente cliente = new Cliente();
        cliente.setNome(nomeField.getText());
        cliente.setSexo((String) sexoField.getSelectedItem());
        cliente.setCpf(cpfField.getText());
        cliente.setEmail(emailField.getText());
        cliente.setCelular(celularField.getText());
        cliente.setEndereco(enderecoField.getText());

        try {
            dao.saveCliente(cliente);
            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarDados() {
        String selectedPet = (String) petComboBox.getSelectedItem();
        if (selectedPet != null) {
            int petId = Integer.parseInt(selectedPet.split("ID: ")[1].replace(")", ""));
            double peso = Double.parseDouble(pesoField.getText());
            double altura = Double.parseDouble(alturaField.getText());
            LocalDate dataInsercao = LocalDate.now();
    
            String sql = "INSERT INTO dados_pet (pet_id, peso, altura, data_insercao) VALUES (?, ?, ?, ?)";
            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, petId);
                stmt.setDouble(2, peso);
                stmt.setDouble(3, altura);
                stmt.setString(4, dataInsercao.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Dados adicionados com sucesso!");
                carregarHistorico(petId);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao adicionar dados do pet.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void carregarHistorico(int petId) {
        String sql = "SELECT peso, altura, data_insercao FROM dados_pet WHERE pet_id = ?";
        List<Object[]> historico = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, petId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double peso = rs.getDouble("peso");
                    double altura = rs.getDouble("altura");
                    LocalDate dataInsercao = LocalDate.parse(rs.getString("data_insercao"));
                    Object[] row = {peso, altura, dataInsercao};
                    historico.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar histórico do pet.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Peso");
        model.addColumn("Altura");
        model.addColumn("Data de Inserção");
    
        for (Object[] row : historico) {
            model.addRow(row);
        }
    
        historicoTable.setModel(model);
    }
    

    private void carregarPets() {
        String sql = "SELECT id, nome FROM pet";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                petComboBox.addItem(nome + " (ID: " + id + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista de pets.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadTreeData() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Clientes");
        treeModel = new DefaultTreeModel(root);
        tree.setModel(treeModel);
    
        String sql = "SELECT cliente.id as cliente_id, cliente.nome as cliente_nome, " +
                "pet.id as pet_id, pet.nome as pet_nome, pet.peso, pet.idade, pet.sexo, pet.especie, pet.raca, " +
                "vacina.id as vacina_id, vacina.nome as vacina_nome, vacina.descricao, vacina.data_aplicacao " +
                "FROM cliente " +
                "LEFT JOIN pet ON cliente.id = pet.proprietario_id " +
                "LEFT JOIN vacina ON pet.id = vacina.pet_id";
    
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            DefaultMutableTreeNode clienteNode = null;
            DefaultMutableTreeNode petNode = null;
    
            int lastClienteId = -1;
            int lastPetId = -1;
    
            while (rs.next()) {
                int clienteId = rs.getInt("cliente_id");
                if (clienteId != lastClienteId) {
                    String clienteNome = rs.getString("cliente_nome");
                    clienteNode = new DefaultMutableTreeNode(clienteNome);
                    root.add(clienteNode);
                    lastClienteId = clienteId;
                }
    
                int petId = rs.getInt("pet_id");
                if (petId != 0 && petId != lastPetId) {
                    String petNome = rs.getString("pet_nome");
                    String petDetails = String.format("%s (Peso: %.2f, Idade: %d, Sexo: %s, Espécie: %s, Raça: %s)",
                            petNome, rs.getDouble("peso"), rs.getInt("idade"), rs.getString("sexo"),
                            rs.getString("especie"), rs.getString("raca"));
                    petNode = new DefaultMutableTreeNode(petDetails);
                    clienteNode.add(petNode);
                    lastPetId = petId;
                }
    
                int vacinaId = rs.getInt("vacina_id");
                if (vacinaId != 0) {
                    String vacinaNome = rs.getString("vacina_nome");
                    String vacinaDescricao = rs.getString("descricao");
                    LocalDate dataAplicacao = LocalDate.parse(rs.getString("data_aplicacao"));
                    String vacinaDetails = String.format("%s (Descrição: %s, Data de Aplicação: %s)",
                            vacinaNome, vacinaDescricao, dataAplicacao);
                    if (petNode != null) {
                        petNode.add(new DefaultMutableTreeNode(vacinaDetails));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados para a árvore.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Database.initialize();
            new PetShopApp().setVisible(true);
        });
    }
}
