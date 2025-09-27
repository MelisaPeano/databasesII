package university.jala.finalProject.dao;

import university.jala.finalProject.config.DataBaseConnection;
import university.jala.finalProject.springJPA.entity.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class CRUDList extends DataBaseConnection {

    public boolean insertList(String listName, String listDescription, int categoryId, int userId) {

        PreparedStatement statement = null;
        PreparedStatement checkStatement = null;
        Connection connection = getConnection();

        try {
            String checkSql = "SELECT category_id FROM Category WHERE category_id = ? AND user_id = ?";
            checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, categoryId);
            checkStatement.setInt(2, userId);

            ResultSet result = checkStatement.executeQuery();

            if (!result.next()) {
                System.out.println("Error the category does not exist or does not belong to the user!!!");
                return false;
            }

            String insertSql = "INSERT INTO List (list_name, list_description, category_id, created_in) VALUES (?, ?, ?, NOW())";
            statement = connection.prepareStatement(insertSql);
            statement.setString(1, listName);
            statement.setString(2, listDescription);
            statement.setInt(3, categoryId);

            int rowsCreated = statement.executeUpdate();

            if (rowsCreated > 0) {
                System.out.println("List succesfully created in the category!!!");
                return true;
            } else {
                System.out.println("Error creating the list!!!");
                return false;
            }

        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<List> allListsInCategory(int categoryId, int userId) {

        PreparedStatement statement = null;
        PreparedStatement checkStatement = null;
        Connection connection = getConnection();
        ArrayList<List> lists = new ArrayList<>();

        try {
            String checkSql = "SELECT category_id FROM Category WHERE category_id = ? AND user_id = ?";
            checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, categoryId);
            checkStatement.setInt(2, userId);

            ResultSet checkResult = checkStatement.executeQuery();

            if (!checkResult.next()) {
                System.out.println("Error the category does not exist or does not belong to the user!!!");
                return lists;
            }

            String selectSql = "SELECT * FROM List WHERE category_id = ?";
            statement = connection.prepareStatement(selectSql);
            statement.setInt(1, categoryId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                List list = new List();
                list.setListId(result.getInt("list_id"));
                list.setCategoryId(result.getInt("category_id"));
                list.setListName(result.getString("list_name"));
                list.setListDescription(result.getString("list_description"));
                list.setCreatedIn(result.getString("created_in"));

                lists.add(list);
            }

            System.out.println(lists.size() + " lists were found in the category!!!");
            return lists;

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return lists;
        }
    }

    public boolean moveListInCategory(int listId, int newCategoryId, int userId) {

        PreparedStatement statement = null;
        PreparedStatement checkStatement = null;
        PreparedStatement checkListStatement = null;
        Connection connection = getConnection();

        try {
            String checkListSql = "SELECT category_id FROM List WHERE list_id = ?";
            checkListStatement = connection.prepareStatement(checkListSql);
            checkListStatement.setInt(1, listId);
            ResultSet listResult = checkListStatement.executeQuery();

            if (!listResult.next()) {
                System.out.println("Error the list does not exist!!!");
                return false;
            }

            int currentCategoryId = listResult.getInt("category_id");

            String checkCategorySql = "SELECT category_id FROM Category WHERE category_id = ? AND user_id = ?";
            checkStatement = connection.prepareStatement(checkCategorySql);
            checkStatement.setInt(1, newCategoryId);
            checkStatement.setInt(2, userId);
            ResultSet categoryResult = checkStatement.executeQuery();

            if (!categoryResult.next()) {
                System.out.println("Error the category does not exist or does not belong to the user!!!");
                return false;
            }

            if (currentCategoryId == newCategoryId) {
                System.out.println("The list is already in this category!!!");
                return true;
            }

            String updateSql = "UPDATE List SET category_id = ? WHERE list_id = ?";
            statement = connection.prepareStatement(updateSql);
            statement.setInt(1, newCategoryId);
            statement.setInt(2, listId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("List succesfully moved to the new category!!!");
                return true;
            } else {
                System.out.println("Error moving the list!!!");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean editNameAndDescription(int listId, String listName, String listDescription, int userId) {

        PreparedStatement statement = null;
        PreparedStatement checkStatement = null;
        Connection connection = getConnection();

        try {
            String checkSql = "SELECT l.list_id FROM List l " +
                    "INNER JOIN Category c ON l.category_id = c.category_id " +
                    "WHERE l.list_id = ? AND c.user_id = ?";
            checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, listId);
            checkStatement.setInt(2, userId);

            ResultSet result = checkStatement.executeQuery();

            if (!result.next()) {
                System.out.println("Error the list does not exist or does not belong to the user!!!");
                return false;
            }

            if (listName == null || listName.trim().isEmpty()) {
                System.out.println("Error the list name cannot be empty!!!");
                return false;
            }

            String updateSql = "UPDATE List SET list_name = ?, list_description = ? WHERE list_id = ?";
            statement = connection.prepareStatement(updateSql);
            statement.setString(1, listName.trim());
            statement.setString(2, listDescription != null ? listDescription.trim() : null);
            statement.setInt(3, listId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("List updated succesfully!!!");
                return true;
            } else {
                System.out.println("Error updating list!!!");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteList(int listId, int userId) {

        PreparedStatement statement = null;
        PreparedStatement checkStatement = null;
        Connection connection = getConnection();

        try {
            String checkSql = "SELECT l.list_id FROM List l " +
                    "INNER JOIN Category c ON l.category_id = c.category_id " +
                    "WHERE l.list_id = ? AND c.user_id = ?";
            checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setInt(1, listId);
            checkStatement.setInt(2, userId);

            ResultSet result = checkStatement.executeQuery();

            if (!result.next()) {
                System.out.println("Error the list does not exist or does not belong to the user!!!");
                return false;
            }

            String deleteSql = "DELETE FROM List WHERE list_id = ?";
            statement = connection.prepareStatement(deleteSql);
            statement.setInt(1, listId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("List succesfully deleted!!!");
                return true;
            } else {
                System.out.println("Error deleting list!!!");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}