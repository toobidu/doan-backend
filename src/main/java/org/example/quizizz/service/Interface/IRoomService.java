package org.example.quizizz.service.Interface;

import org.example.quizizz.model.dto.room.*;
import org.example.quizizz.repository.RoomRepository;

import java.util.List;

/**
 * Interface cho service quản lý phòng chơi
 */
public interface IRoomService {

    /**
     * Tạo phòng mới và tự động join vào phòng với quyền host
     *
     * @param request thông tin tạo phòng
     * @param userId  ID người tạo phòng
     * @return thông tin phòng đã tạo
     */
    RoomResponse createRoom(CreateRoomRequest request, Long userId);

    /**
     * Join vào phòng bằng room code
     *
     * @param request thông tin join phòng
     * @param userId  ID người join
     * @return thông tin phòng
     */
    RoomResponse joinRoom(JoinRoomRequest request, Long userId);

    /**
     * Rời khỏi phòng
     *
     * @param roomId ID phòng
     * @param userId ID người rời phòng
     */
    void leaveRoom(Long roomId, Long userId);

    /**
     * Lấy danh sách người chơi trong phòng
     *
     * @param roomId ID phòng
     * @return danh sách người chơi
     */
    List<RoomPlayerResponse> getRoomPlayers(Long roomId);

    /**
     * Kick người chơi khỏi phòng (chỉ host mới có quyền)
     *
     * @param roomId  ID phòng
     * @param request thông tin kick
     * @param hostId  ID host
     */
    void kickPlayer(Long roomId, KickPlayerRequest request, Long hostId);

    /**
     * Mời người chơi vào phòng
     *
     * @param request   thông tin mời
     * @param inviterId ID người mời
     * @return thông tin lời mời
     */
    InvitationResponse invitePlayer(InvitePlayerRequest request, Long inviterId);

    /**
     * Phản hồi lời mời (chấp nhận hoặc từ chối)
     *
     * @param invitationId ID lời mời
     * @param accept       true nếu chấp nhận, false nếu từ chối
     * @param userId       ID người được mời
     * @return thông tin phòng nếu chấp nhận
     */
    RoomResponse respondToInvitation(Long invitationId, boolean accept, Long userId);

    /**
     * Lấy danh sách lời mời của user
     *
     * @param userId ID user
     * @return danh sách lời mời
     */
    List<InvitationResponse> getUserInvitations(Long userId);

    /**
     * Cập nhật thông tin phòng
     *
     * @param roomId  ID phòng
     * @param request thông tin cập nhật
     * @param userId  ID người cập nhật
     * @return thông tin phòng đã cập nhật
     */
    RoomResponse updateRoom(Long roomId, UpdateRoomRequest request, Long userId);

    /**
     * Xóa phòng
     *
     * @param roomId ID phòng
     * @param userId ID người xóa
     */
    void deleteRoom(Long roomId, Long userId);

    /**
     * Lấy thông tin phòng theo ID
     *
     * @param roomId ID phòng
     * @return thông tin phòng
     */
    RoomResponse getRoomById(Long roomId);

    /**
     * Lấy thông tin phòng theo room code
     *
     * @param roomCode mã phòng
     * @return thông tin phòng
     */
    RoomResponse getRoomByCode(String roomCode);

    /**
     * Lấy tất cả phòng đang WAITING với phân trang và tìm kiếm (đơn giản, không
     * filter phức tạp)
     *
     * @param page   số trang (bắt đầu từ 0)
     * @param size   số phòng mỗi trang
     * @param search từ khóa tìm kiếm (optional)
     * @return kết quả phân trang
     */
    PagedRoomResponse getAllRoomsSimple(int page, int size, String search);

    /**
     * Join phòng trực tiếp bằng room ID (cho phòng public)
     *
     * @param roomId ID phòng
     * @param userId ID người join
     * @return thông tin phòng
     */
    RoomResponse joinRoomById(Long roomId, Long userId);

    /**
     * Chuyển quyền host
     *
     * @param roomId        ID phòng
     * @param newHostId     ID host mới
     * @param currentHostId ID host hiện tại
     * @return thông tin phòng
     */
    RoomResponse transferHost(Long roomId, Long newHostId, Long currentHostId);

    /**
     * Bắt đầu game
     *
     * @param roomId ID phòng
     * @param userId ID host
     */
    void startGame(Long roomId, Long userId);

    /**
     * Kiểm tra user có phải host không
     *
     * @param roomId ID phòng
     * @param userId ID user
     * @return true nếu là host
     */
    boolean isRoomHost(Long roomId, Long userId);

    /**
     * Kiểm tra user có trong phòng không
     *
     * @param roomId ID phòng
     * @param userId ID user
     * @return true nếu có trong phòng
     */
    boolean isUserInRoom(Long roomId, Long userId);
    
    /**
     * Lấy RoomRepository
     * @return RoomRepository
     */
    RoomRepository getRoomRepository();
}
