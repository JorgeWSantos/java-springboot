package br.com.jorgesantos.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.jorgesantos.todolist.dto.ErrorResponse;
import br.com.jorgesantos.todolist.dto.SuccessResponse;
import br.com.jorgesantos.todolist.utils.utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private ITaskRepository taskRepository;

    @GetMapping("")
    public ResponseEntity listById(HttpServletRequest request) {
        try {
            List<TaskModel> list = this.taskRepository.findByIdUser((UUID) request.getAttribute("idUser"));

            SuccessResponse<List<TaskModel>> response = new SuccessResponse<List<TaskModel>>();
    
            response.setSuccess(true);
            response.setData(list);
    
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setSuccess(false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }

    @PostMapping("")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        try {
            taskModel.setIdUser((UUID)request.getAttribute("idUser"));

            ErrorResponse errorResponse = new ErrorResponse();
            var currentDate = LocalDateTime.now();
    
            if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
                errorResponse.setMessage("A data de início/término deve ser maior que a data atual");
                errorResponse.setSuccess(false);
    
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
    
            if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
                errorResponse.setMessage("A data de início deve ser menor que a data de término");
                errorResponse.setSuccess(false);
    
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
    
            var task = this.taskRepository.save(taskModel);

            return ResponseEntity.ok(task);
        } 
        catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setSuccess(false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        try {
            ErrorResponse errorResponse = new ErrorResponse();
            var task = this.taskRepository.findById(id).orElse(null);

            System.out.println("task: " + task);

            if (task == null) {
                errorResponse.setMessage("Tarefa não encontrada");
                errorResponse.setSuccess(false);
    
                return ResponseEntity.status(400).body(errorResponse);
            }

            if(!task.getIdUser().equals(request.getAttribute("idUser"))) {
                errorResponse.setMessage("O usuário não tem permissão para alterar essa tarefa");
                errorResponse.setSuccess(false);
    
                return ResponseEntity.status(400).body(errorResponse);
            }
    
            utils.copyNonNullProperties(taskModel, task);
    
            var taskUpdated = this.taskRepository.save(task);
    
            return ResponseEntity.status(200).body(taskUpdated);
        } catch (Exception e) {
            // TODO: handle exception
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setSuccess(false);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }
}
