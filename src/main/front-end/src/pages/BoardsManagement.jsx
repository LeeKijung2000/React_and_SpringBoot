import { useEffect, useState } from "react";
// import "./PagesCommon.module.css";
import common from "./PagesCommon.module.css";
import { useSearchParams } from "react-router-dom";


const BoardsManagement = () => {

    const [boards, setBoards] = useState([]);
    const [searchParams, setSearchParams] = useSearchParams(); //URL의 쿼리 파라미터 관리
    const currentPage = parseInt(searchParams.get('page') || '1' );
    const [pageInfo, setPageInfo] = useState(null);

    useEffect(() => {
        fetchBoards(currentPage);
    }, [currentPage])

    const fetchBoards = page => {
        fetch(`/react/admin/boards?page=${page}`)
            .then(res => res.json())
            .then(data => {
                console.log(data)
                setBoards(data.list)
                setPageInfo(data.pi)
            }
            )
            .catch(err => console.log(err))
    }

    const changePage = page => {
        setSearchParams({page:page.toString()})
    }

    const handleStatusToggle = (id, value) => {
        fetch('/react/admin/status', {
            method: 'put',
            headers: {'content-type': 'application/json; charset=UTF-8'},
            body: JSON.stringify({
                boardId: id,
                boardStatus: value
            })

        })
        .then(res => res.json())
        .then(data => {
            if(data == 1) {
                setBoards(prev => prev.map(b => 
                    b.boardId == id
                    ? {...b, boardStatus:value}
                    : b
                ))

                if(selectBoard && selectBoard.boardId == id) {
                    setSelectBoard({...selectBoard, boardStatus:value})
                }


            } else {
                alert("상태변경 실패")
                location.reload();
            }
        })
        .catch(err => console.log(err))
    }

    const [selectBoard, setSelectBoard] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const handleBoardClick = board => {
        setSelectBoard(board);
        setShowModal(true);
    }

    const closeModal = () => {
        setShowModal(false);
        setSelectBoard(null);
    }

    return (
        <>
            <div className="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <h1 className="h2">Boards</h1>
            </div>

            <div className="bd-example">
                <table className={`table table-hover ${common['table']}`} style={{ textAlign: "center" }}>
                    <thead>
                        <tr>
                            <th>NO.</th>
                            <th>TITLE</th>
                            <th>WRITER</th>
                            <th>CREATE</th>
                            <th>MODIFY</th>
                            <th>VIEWS</th>
                            <th>STATUS</th>
                        </tr>
                    </thead>
                    <tbody>
                        {boards.map(board => {
                            return (
                                <tr key={board.boardId}>
                                    <td onClick={() => handleBoardClick(board)}>{board.boardId}</td>
                                    <td onClick={() => handleBoardClick(board)}>{board.boardTitle}</td>
                                    <td onClick={() => handleBoardClick(board)}>{board.nickName}</td>
                                    <td onClick={() => handleBoardClick(board)}>{board.boardCreateDate}</td>
                                    <td onClick={() => handleBoardClick(board)}>{board.boardModifyDate}</td>
                                    <td onClick={() => handleBoardClick(board)}>{board.boardCount}</td>
                                    <td>
                                        <div className={board.boardStatus === 'Y' ? common.select : ''}
                                        onClick={() => board.boardStatus == 'N' ? handleStatusToggle(board.boardId, 'Y') : null}
                                        >Y</div>
                                        <div className={board.boardStatus === 'N' ? common.select : ''}
                                        onClick={() => board.boardStatus == 'Y' ? handleStatusToggle(board.boardId, 'N') : null}
                                        >N</div>
                                    </td>
                                </tr>
                            );
                        })}

                    </tbody>
                </table>
            </div>

            {/* 조건부 렌더링 */}
            { pageInfo && (
            <nav aria-label="Standard pagination example" style={{ float: "right" }}>
                <ul className="pagination">
                    <li className={`page-item ${currentPage <= 1 ? 'disabled' : ''}`}>
                        <button className="page-link" onClick={() => currentPage > 1 && changePage(currentPage - 1)} disabled={currentPage <= 1} aria-label="Previous">
                            <span aria-hidden="true">&laquo;</span>
                        </button>
                    </li>

                    {Array.from(
                        {length:pageInfo.endPage - pageInfo.startPage + 1},
                        (_, index) => pageInfo.startPage + index
                    ).map(pageNum => {
                    return (
                        <li className={`page-item ${currentPage == pageNum ? 'active' : ''}`}  key={pageNum}>
                            <button className="page-link" onClick={() => changePage(pageNum)}>{pageNum}</button>
                        </li>
                    );
                    })}

                    <li className={`page-item ${currentPage >= pageInfo.maxPage ? 'disabled' : ''}`}>
                    <button className="page-link" onClick={() => currentPage < pageInfo.maxPage && changePage(currentPage + 1)}disabled={currentPage >= pageInfo.maxPage}aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </button>
                    </li>
                </ul>
            </nav>
            )}
            


            {showModal && selectBoard && (
                <div style={{display: 'block'}} className="modal show" id="exampleModalCenteredScrollable" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5" id="exampleModalCenteredScrollableTitle">{selectBoard.boardTitle}</h1>
                                <button type="button" className="btn-close" onClick={closeModal} aria-label="Close"></button>
                            </div>
                            <div className="modal-body">{selectBoard.boardContent}</div>
                            <div className="modal-body" style={{ textAlign: 'right', fontSize: '14px' }}>작성자 : {selectBoard.nickName}</div>
                            <div className="modal-footer">
                                <button type="button" 
                                className={selectBoard.boardStatus == 'N' ? 'btn btn-primary' : 'btn btn-dark'}
                                onClick={() => selectBoard.boardStatus == 'Y' 
                                    ? handleStatusToggle(selectBoard.boardId, 'N') : handleStatusToggle(selectBoard.boardId, 'Y')}
                                > 
                                {selectBoard.boardStatus == 'N' ? '게시글올리기' : '게시글 내리기'}
                                </button>
                                <button type="button" className="btn btn-secondary" onClick={closeModal}>Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            {showModal && <div className="modal-backdrop fade show"></div>}

        </>
    );
}

export default BoardsManagement;