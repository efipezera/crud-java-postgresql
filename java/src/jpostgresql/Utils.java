package jpostgresql;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Utils {

    static Scanner teclado = new Scanner(System.in);

    public static Connection conectar() {
        Properties props = new Properties();
        props.setProperty("user", "seuusuario");
        props.setProperty("password", "suasenha");
        props.setProperty("ssl", "false");
        String URL_SERVIDOR = "jdbc:postgresql://localhost:porta/nomedobanco";

        try{
            return DriverManager.getConnection(URL_SERVIDOR, props);
        }catch (Exception e) {
            e.printStackTrace();
            if(e instanceof ClassNotFoundException) {
                System.err.println("Verifique o driver de conexão.");
            } else {
                System.err.println("Verifique se o servidor está ativo.");
            }
            System.exit(-42);
            return null;
        }
    }

    public static void desconectar(Connection conn) {
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void listar() {
        String BUSCAR_TODOS = "SELECT * FROM produtos;";

        try {
            Connection conn = conectar();
            PreparedStatement produtos = conn.prepareStatement(
                    BUSCAR_TODOS,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            ResultSet res = produtos.executeQuery();
            res.last();
            int quantidadeLinhas = res.getRow();
            res.beforeFirst();
            if(quantidadeLinhas > 0) {
                System.out.println("Listando produtos...");
                while(res.next()){
                    System.out.println("---------------------------------");
                    System.out.println("ID: "+res.getInt(1));
                    System.out.println("Produto: "+res.getString(2));
                    System.out.println("Preço: "+res.getFloat(3));
                    System.out.println("Estoque: "+res.getInt(4));
                }
            } else {
                System.out.println("Não existem produtos cadastrados.");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Erro ao buscar todos os produtos.");
            System.exit(-42);
        }
    }

    public static void inserir() {
        System.out.println("Informe o nome do produto: ");
        String nome = teclado.nextLine();
        System.out.println("Informe o preço do produto: ");
        float preco = teclado.nextFloat();
        System.out.println("Informe a quantidade em estoque: ");
        int estoque = teclado.nextInt();
        String INSERIR = "INSERT INTO produtos (nome, preco, estoque) VALUES (?, ?, ?)";

        try {
            Connection conn = conectar();
            PreparedStatement salvar = conn.prepareStatement(INSERIR);
            salvar.setString(1, nome);
            salvar.setFloat(2, preco);
            salvar.setInt(3, estoque);
            salvar.executeUpdate();
            salvar.close();
            desconectar(conn);
            System.out.println("O produto "+nome+" foi inserido com sucesso.");
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao salvar produto.");
            System.exit(-42);
        }
    }


    public static void atualizar() {
        System.out.println("Informe o código do produto: ");
        int id = Integer.parseInt(teclado.nextLine());
        String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id = ?";

        try {
            Connection conn = conectar();
            PreparedStatement produto = conn.prepareStatement(
                    BUSCAR_POR_ID,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            produto.setInt(1, id);
            ResultSet res = produto.executeQuery();
            res.last();
            int quantidade = res.getRow();
            res.beforeFirst();
            if(quantidade > 0){
                System.out.println("Informe o nome do produto: ");
                String nome = teclado.nextLine();
                System.out.println("Informe o preço do produto: ");
                float preco = teclado.nextFloat();
                System.out.println("Informe a quantidade em estoque: ");
                int estoque = teclado.nextInt();
                String ATUALIZAR = "UPDATE produtos SET nome = ?, preco = ?, estoque = ? WHERE id = ?";
                PreparedStatement atualizar = conn.prepareStatement(ATUALIZAR);
                atualizar.setString(1, nome);
                atualizar.setFloat(2, preco);
                atualizar.setInt(3, estoque);
                atualizar.setInt(4, id);
                atualizar.executeUpdate();
                atualizar.close();
                desconectar(conn);
                System.out.println("O produto "+nome+" foi atualizado com sucesso.");
            } else {
                System.out.println("Não existe produto com o ID informado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao atualizar produto.");
            System.exit(-42);
        }
    }

    public static void deletar() {
        String DELETAR = "DELETE FROM produtos WHERE id = ?";
        String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id = ?";
        System.out.println("Informe o código do produto: ");
        int id = Integer.parseInt(teclado.nextLine());

        try {
            Connection conn = conectar();
            PreparedStatement produto = conn.prepareStatement(
                    BUSCAR_POR_ID,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            produto.setInt(1, id);
            ResultSet res = produto.executeQuery();
            res.last();
            int quantidade = res.getRow();
            res.beforeFirst();
            if(quantidade > 0) {
                PreparedStatement deletar = conn.prepareStatement(DELETAR);
                deletar.setInt(1, id);
                deletar.executeUpdate();
                deletar.close();
                desconectar(conn);
                System.out.println("O produto foi deletado com sucesso.");
            } else {
                System.out.println("Não existe produto com o ID informado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao deletar o produto.");
            System.exit(-42);
        }
    }

    public static void menu() {
        System.out.println("===============Gerenciamento de Produtos===============");
        System.out.println("Selecione uma opção: ");
        System.out.println("1 - Listar produtos.");
        System.out.println("2 - Inserir produtos.");
        System.out.println("3 - Atualizar produtos.");
        System.out.println("4 - Deletar produtos.");

        int opcao = Integer.parseInt(teclado.nextLine());
        switch (opcao) {
            case 1 -> listar();
            case 2 -> inserir();
            case 3 -> atualizar();
            case 4 -> deletar();
            default -> System.out.println("Opção inválida.");
        }
    }
}