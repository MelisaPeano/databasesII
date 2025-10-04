package university.jala.finalProject.springJPA.service;

import university.jala.finalProject.springJPA.entity.List;
import java.util.Collection;

public interface ListService {
    List createList(String listName, String listDescription, Integer categoryId, Integer userId);
    Collection<List> getAllListsInCategory(Integer categoryId, Integer userId);
    boolean moveListToCategory(Integer listId, Integer newCategoryId, Integer userId);
    List updateList(Integer listId, String listName, String listDescription, Integer userId);
    boolean deleteList(Integer listId, Integer userId);
    boolean listBelongsToUser(Integer listId, Integer userId);
}
