package com.example.petshop;

import java.sql.*;
import java.text.SimpleDateFormat;

public class PetShopDAO {

    private Connection getConnection() throws SQLException {
        return Database.getConnection();
    }

    public void saveCliente(Cliente cliente) throws Exception {
        if (existsCliente(cliente.getCpf())) {
            throw new Exception("Cliente com CPF já cadastrado!");
        }

        String sql = "INSERT INTO cliente(nome, sexo, cpf, email, celular, endereco) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getSexo());
            pstmt.setString(3, cliente.getCpf());
            pstmt.setString(4, cliente.getEmail());
            pstmt.setString(5, cliente.getCelular());
            pstmt.setString(6, cliente.getEndereco());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean existsCliente(String cpf) {
        String sql = "SELECT count(*) FROM cliente WHERE cpf = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpf);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void savePet(Pet pet) {
        String sql = "INSERT INTO pet(nome, proprietario_id, peso, idade, sexo, especie, raca) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pet.getNome());
            pstmt.setInt(2, pet.getProprietarioId());
            pstmt.setDouble(3, pet.getPeso());
            pstmt.setInt(4, pet.getIdade());
            pstmt.setString(5, pet.getSexo());
            pstmt.setString(6, pet.getEspecie());
            pstmt.setString(7, pet.getRaca());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void atualizarPeso(int petId, double novoPeso) throws SQLException {
        String sql = "UPDATE pet SET peso = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, novoPeso);
            pstmt.setInt(2, petId);
            pstmt.executeUpdate();
        }
    }

    public void criarUsuario(String nome, String sexo, String email, String admcode, String senha) throws Exception {
        if (!admcode.equals("123")) {
            throw new Exception("Senha de administrador inválida para criar usuário.");
        }

        String sql = "INSERT INTO usuario (nome, sexo, email, senha) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, sexo);
            pstmt.setString(3, email);
            pstmt.setString(4, senha);
            pstmt.executeUpdate();
        }
    }

    public boolean verificarLogin(String username, String senha) {
        String sql = "SELECT count(*) FROM usuario WHERE email = ? AND senha = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, senha);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveVacina(Vacina vacina) {
        String sql = "INSERT INTO vacina(nome, descricao, data_aplicacao, data_hora_cadastro, pet_id) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vacina.getNome());
            pstmt.setString(2, vacina.getDescricao());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            pstmt.setString(3, dateFormat.format(vacina.getDataAplicacao()));
            pstmt.setString(4, dateTimeFormat.format(vacina.getDataHoraCadastro()));
            pstmt.setInt(5, vacina.getPetId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
