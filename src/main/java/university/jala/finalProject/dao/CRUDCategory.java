package university.jala.finalProject.dao;

import university.jala.finalProject.config.DataBaseConnection;
import university.jala.finalProject.springJPA.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CRUDCategory extends DataBaseConnection {

    public boolean insertCategory(Category category) {

        PreparedStatement statement = null;
        PreparedStatement checkStatement = null;
        Connection connection = getConnection();

        try {
            String checkSQL = "SELECT COUNT(*) FROM Category WHERE user_id = ? AND category_name = ?";
            checkStatement = connection.prepareStatement(checkSQL);
            checkStatement.setInt(1, category.getUser_id());
            checkStatement.setString(2, category.getCategoryName());

            ResultSet rs = checkStatement.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("There is already a Category with that name");
                return false;
            }

            String sql = "INSERT INTO Category (user_id, category_name, category_color, created_in) VALUES (?, ?, ?, ?, ?)";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, category.getUser_id());
            statement.setString(2, category.getCategoryName());
            statement.setString(3, category.getCategoryColor());
            statement.setString(4, category.getCreatedIn().toString());
            statement.execute();
            return true;
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
            return false;
        } finally {
            try {
                if (checkStatement != null) {
                    checkStatement.close();
                }
                if (statement != null) {
                    statement.close();
                }
                connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean updateCategoryName(Category category) {

        PreparedStatement statement = null;
        Connection connection = getConnection();

        String sql = "UPDATE Category SET category_name = ? WHERE category_id = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, category.getCategoryName());
            statement.setInt(2, category.getId());
            statement.execute();
            return true;
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
            return false;
        } finally {
            try {
                if (statement != null) statement.close();
                connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public boolean deleteCategory(Category category) {

        PreparedStatement checkStatement = null;
        PreparedStatement deleteStatement = null;
        Connection connection = getConnection();

        try {

            String checkSql = "SELECT COUNT(*) FROM List WHERE category_id = ?";
            checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, category.getId());

            ResultSet rs = checkStatement.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Cannot delete, category has associated lists!!!");
                return false;
            }

            String deleteSql = "DELETE FROM Category WHERE category_id = ?";
            deleteStatement = connection.prepareStatement(deleteSql);
            deleteStatement.setInt(1, category.getId());
            deleteStatement.execute();

            return true;

        } catch (SQLException e) {
            System.err.println("ERROR: " + e.getMessage());
            return false;
        } finally {
            try {
                if (checkStatement != null) {
                    checkStatement.close();
                }
                if (deleteStatement != null) {
                    deleteStatement.close();
                }
                connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public List<Category> readCategoriesByUser(int userId) {

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection connection = getConnection();
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT * FROM Category WHERE user_id = ? ORDER BY category_name";

        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt("category_id"));
                category.setUser_id(resultSet.getInt("user_id"));
                category.setCategoryName(resultSet.getString("category_name"));
                category.setCategoryColor(resultSet.getString("category_color"));
                category.setCreatedIn(resultSet.getString("created_in"));

                categories.add(category);
            }

            return categories;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return categories;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
