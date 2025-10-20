package university.jala.finalProject.springJPA.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import university.jala.finalProject.springJPA.entity.List;
import university.jala.finalProject.springJPA.repository.ListRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class ListService {

    @Autowired
    private ListRepository listRepository;

    /**
     * Obtener todas las listas de una categoría
     */
    public Collection<List> getListsByCategory(Integer categoryId) {
        return listRepository.findByCategory_Id(categoryId);
    }

    /**
     * Obtener listas por categoría verificando permisos de usuario
     */
    public Collection<List> getListsByCategoryAndUser(Integer categoryId, Integer userId) {
        return listRepository.findByCategoryIdAndUserId(categoryId, userId);
    }

    /**
     * Obtener una lista específica verificando que pertenezca al usuario
     */
    public Optional<List> getListByIdAndUser(Integer listId, Integer userId) {
        return listRepository.findByIdAndUserId(listId, userId);
    }

    /**
     * Guardar o actualizar una lista
     */
    public List saveList(List list) {
        // Puedes agregar lógica de validación aquí
        if (list.getCreatedIn() == null) {
            list.setCreatedIn(java.time.Instant.now());
        }
        return listRepository.save(list);
    }

    /**
     * Eliminar una lista por ID
     */
    public boolean deleteList(Integer listId) {
        if (listRepository.existsById(listId)) {
            listRepository.deleteById(listId);
            return true;
        }
        return false;
    }

    /**
     * Verificar si una lista existe en una categoría específica
     */
    public boolean existsListInCategory(Integer listId, Integer categoryId) {
        return listRepository.existsByIdAndCategoryId(listId, categoryId);
    }

    /**
     * Obtener todas las listas
     */
    public Collection<List> getAllLists() {
        return listRepository.findAll();
    }

    /**
     * Obtener una lista por ID
     */
    public Optional<List> getListById(Integer listId) {
        return listRepository.findById(listId);
    }

    /**
     * Verificar si el usuario tiene permisos sobre la lista
     */
    public boolean hasUserAccessToList(Integer listId, Integer userId) {
        return listRepository.findByIdAndUserId(listId, userId).isPresent();
    }
}