package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

public class Program {

	public static void main(String[] args) throws SQLException {

		//request para conecao com o banco
		Connection conn = DB.getConnection();

		//obj que envia a query para o banco
		Statement st = conn.createStatement();

		//resultado de consulta em forma de tabela
		//ResultSet rs = st.executeQuery("select * from tb_product");
		//ResultSet rsOrder = st.executeQuery("select * from tb_order");

		ResultSet rs = st.executeQuery("SELECT * FROM tb_order " +
				"INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id " +
				"INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id ");


		//colecao de pares chave e valor
		Map<Long, Order> mapOrder = new HashMap<>();
		Map<Long, Product> mapProduct = new HashMap<>();

		//comando para modelar as linhas
		while (rs.next()) {

			Long orderId = rs.getLong("order_id");
			if (mapOrder.get(orderId) == null) {

				Order order = instantiateOrder(rs);
				mapOrder.put(orderId, order);
			}

			Long productId = rs.getLong("product_id");
			if (mapProduct.get(productId) == null){

				Product product = instantiateProduct(rs);
				mapProduct.put(productId, product);
			}

			//inserindo produto na lista de pedido associando as tabelas
			mapOrder.get(orderId).getProducts().add(mapProduct.get(productId));
		}

		for (Long orderId : mapOrder.keySet()) {
			System.out.println(mapOrder.get(orderId));
			for (Product product : mapOrder.get(orderId).getProducts()) {
				System.out.println(product);
			}

			System.out.println();
		}
	}

	private static Order instantiateOrder(ResultSet rsOrder) throws SQLException {
		Order order = new Order();
		order.setId(rsOrder.getLong("order_id"));
		order.setLatitude(rsOrder.getDouble("latitude"));
		order.setLongitude(rsOrder.getDouble("longitude"));
		order.setMoment(rsOrder.getTimestamp("moment").toInstant());
		order.setStatus(OrderStatus.values()[rsOrder.getInt("status")]);

		return order;
	}

	private static Product instantiateProduct(ResultSet rs) throws SQLException {

		//instanciado cada produto pegando todos os atributos
		//o nome da coluna tem que ser igual ao banco
		Product p = new Product();
		p.setId(rs.getLong("product_id"));
		p.setDescription(rs.getString("description"));
		p.setName(rs.getString("name"));
		p.setImageUri(rs.getString("image_uri"));
		p.setPrice(rs.getDouble("price"));

		//retorno do metodo
		return p;
	}
}
