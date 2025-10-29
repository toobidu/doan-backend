package org.example.quizizz.repository;

import org.example.quizizz.model.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho quản lý phòng chơi
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Tìm phòng theo room code
     */
    Optional<Room> findByRoomCode(String roomCode);

    /**
     * Kiểm tra room code đã tồn tại chưa
     */
    boolean existsByRoomCode(String roomCode);

    /**
     * Tìm kiếm phòng public theo tên (loại trừ ARCHIVED)
     */
    @Query("SELECT r FROM Room r WHERE r.isPrivate = false AND r.roomName LIKE %:roomName% AND r.status != 'ARCHIVED'")
    List<Room> findPublicRoomsByRoomNameContaining(@Param("roomName") String roomName);

    /**
     * Lấy danh sách phòng của owner có phân trang
     */
    @Query("SELECT r FROM Room r WHERE r.ownerId = :ownerId ORDER BY r.createdAt DESC")
    Page<Room> findByOwnerIdWithPagination(@Param("ownerId") Long ownerId, Pageable pageable);

    /**
     * Tìm kiếm phòng của owner theo tên có phân trang
     */
    @Query("SELECT r FROM Room r WHERE r.ownerId = :ownerId AND r.roomName LIKE %:search% ORDER BY r.createdAt DESC")
    Page<Room> findByOwnerIdAndSearch(@Param("ownerId") Long ownerId, @Param("search") String search,
                                      Pageable pageable);

    /**
     * Lấy tất cả phòng theo trạng thái có phân trang (bao gồm cả private và public)
     */
    @Query("SELECT r FROM Room r WHERE r.status = :status AND r.status != 'ARCHIVED' ORDER BY r.createdAt DESC")
    Page<Room> findAllRoomsByStatusWithPagination(@Param("status") String status, Pageable pageable);

    /**
     * Tìm kiếm tất cả phòng theo trạng thái và tên có phân trang (bao gồm cả
     * private và public)
     */
    @Query("SELECT r FROM Room r WHERE r.status = :status AND r.status != 'ARCHIVED' AND r.roomName LIKE %:search% ORDER BY r.createdAt DESC")
    Page<Room> findAllRoomsByStatusAndSearch(@Param("status") String status, @Param("search") String search,
                                             Pageable pageable);
}
