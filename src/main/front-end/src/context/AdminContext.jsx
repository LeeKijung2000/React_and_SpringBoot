// Context API : React 내부 상태 관리
// React 앱 내부에서 상태 관리 차원으로 데이터 정보 공유 시 사용
// react 컴포넌트끼리의 상태를 공유하기 위한 도구

import { Children, createContext, useEffect, useRef, useState } from "react";

// 사용법1. context 생성
export const AdminContext = createContext(null);  // .Provider컴포넌트 자동 생성: context데이터를 하위 컴포넌트한테 제공하는 역할

// 사용법2. Provider로 감싸기
export const AdminProvider = ({children}) => {
    // react에서 값을 저장할 수 있는 곳, 값이 바뀌어도 컴포넌트를 리렌더링 하지 않음
    // .current 속성에 값이 저장됨
    const calledRef = useRef(false);
    const [loading, setLoading] = useState(null);
    const [admin, setAdmin] = useState();

    useEffect(() => {
        if (calledRef.current) return;
        calledRef.current = true;

        fetch("react/admin/users", {
            method: 'get',
            credentials: 'include', // 어떤 인증 정보를 포함할 것인가
            headers: { fetch: true }
        })
            .then(res => {
                if (res.status == 403) {
                    alert('접근권한이 없습니다.')
                    location.href = 'http://localhost:8080/home';
                    return null;
                } else {
                    setLoading(true);
                }
                return res.json();
            })
            .then(data => setAdmin(data))
            .catch(err => console.log(err))
    }, [])

    if (!loading) return null;

    const value = { admin };
    return(
        <AdminContext.Provider value={value}>
            {children}
        </AdminContext.Provider>
    )
}