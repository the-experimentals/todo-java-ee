package com.todo.sqlserver;

import com.todo.datamodels.Todo;
import com.todo.datamodels.TodoFolder;
import com.todo.properties.MSSqlServerProps;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class SQLServerManager {

    public List<TodoFolder> fetchTodoFolders() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        List<TodoFolder> folders = new ArrayList<>();
        Class.forName(MSSqlServerProps.DB_DRIVER).getDeclaredConstructor().newInstance();
        try(Connection conn = DriverManager.getConnection(MSSqlServerProps.CONNECTION_STRING)){
            try(PreparedStatement ps = conn.prepareStatement(SQLQueries.FETCH_TODO_FOLDERS); ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    folders.add(new TodoFolder(){{
                        setID(rs.getInt("ID"));
                        setNAME(rs.getString("NAME"));
                        setDESCRIPTION(rs.getString("DESCRIPTION"));
                        setCREATED_ON(rs.getTimestamp("CREATED_ON").toInstant().atZone(TimeZone.getDefault().toZoneId()));
                    }});
                }
            }
        }
        return folders;
    }

    public boolean saveTodoFolder(TodoFolder folder) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        boolean isSaved = false;
        Class.forName(MSSqlServerProps.DB_DRIVER).getDeclaredConstructor().newInstance();
        int INDEX = 0;
        try(Connection conn = DriverManager.getConnection(MSSqlServerProps.CONNECTION_STRING)){
            try(PreparedStatement ps = conn.prepareStatement(SQLQueries.INSERT_TODO_FOLDERS)){

                Timestamp timestamp = Timestamp.from(Instant.now());

                ps.setString(++INDEX,folder.getNAME());
                ps.setString(++INDEX, folder.getDESCRIPTION());
                ps.setTimestamp(++INDEX, timestamp);
                ps.setTimestamp(++INDEX, timestamp);

                isSaved = ps.executeUpdate() > 0;
            }
        }

        return isSaved;
    }

    public boolean deleteTodoFolder(int folderID) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        boolean idDeleted = false;
        int INDEX = 0;
        Class.forName(MSSqlServerProps.DB_DRIVER).getDeclaredConstructor().newInstance();
        try(Connection conn = DriverManager.getConnection(MSSqlServerProps.CONNECTION_STRING)){
            try(PreparedStatement ps = conn.prepareStatement(SQLQueries.DELETE_TODO_FOLDERS)){
                ps.setInt(++INDEX, folderID);

                idDeleted = ps.executeUpdate() > 0;
            }
        }
        return idDeleted;
    }

    public List<Todo> fetchTodos(int folderID) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException{
        List<Todo> todos = new ArrayList<>();
        int INDEX = 0;
        Class.forName(MSSqlServerProps.DB_DRIVER).getDeclaredConstructor().newInstance();
        try(Connection conn = DriverManager.getConnection(MSSqlServerProps.CONNECTION_STRING)){
            try(PreparedStatement ps = conn.prepareStatement(SQLQueries.FETCH_TODOS)){
                ps.setInt(++INDEX, folderID);

                try(ResultSet rs = ps.executeQuery()) {
                    while (rs.next()){
                        todos.add(new Todo(){{
                            setID(rs.getInt("ID"));
                            setNAME(rs.getString("NAME"));
                            setDESCRIPTION(rs.getString("DESCRIPTION"));
                            setFOLDER_ID(rs.getInt("FOLDER_ID"));
                            setCREATED_ON(rs.getTimestamp("CREATED_ON").toInstant().atZone(TimeZone.getDefault().toZoneId()));
                            setMODIFIED_ON(rs.getTimestamp("MODIFIED_ON").toInstant().atZone(TimeZone.getDefault().toZoneId()));
                        }});
                    }
                }
            }
        }

        return todos;
    }

}
