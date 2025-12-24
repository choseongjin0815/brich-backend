(작성 완료되면 불필요한 커밋 히스토리 남기지 않기 위해 머지없이 해당 브랜치 지우고 README.md 파일만 main등에 업로드할 예정)

#  Brich 프로젝트 소개 
**Brich**는 광고주와 블로거를 연결하는 캠페인 매칭 플랫폼이자, 데이터 기반 블로거 성장 지원 서비스입니다.

광고주는 캠페인(광고)을 등록하고, 블로거는 자신에게 맞는 캠페인에 지원하여 협업할 수 있습니다. 
더 나아가, 크롤링한 데이터를 정제하여 산출한 독자적인 블로그 지수와 황금 키워드를 제공함으로써 
블로거가 데이터 기반으로 전략을 수립하고 성장할 수 있도록 적극 지원합니다.

### 주요 특징
- 📢 **캠페인 매칭**: 광고주와 블로거를 효율적으로 연결
- 📊 **독자적 블로그 지수**: 크롤링 데이터 정제를 통한 자체 블로그 분석 지표 제공
- 🔑 **황금 키워드 제공**: 데이터 기반 최적 키워드 추천
- 🎯 **맞춤형 캠페인 추천**: 블로거 특성에 맞는 캠페인 매칭
- 💬 **실시간 1:1 채팅**: 블로거와 광고주 간 원활한 소통 지원

🔗 **[Figma 디자인 보기](https://www.figma.com/design/N2J1QrbHshX2X0FL1Vnnoo/Brich?node-id=0-1&p=f&t=ASVuOO4T5JVYLbNz-0)**
(유튜브 시연 영상 추가 예정...)

---

## 목차
- [기술 스택](#-기술-스택)
- [주요 기능](#-주요-기능)
- [프로젝트 아키텍처](#-프로젝트-아키텍처)
- [ERD](#-erd)
- [팀원](#-팀원)

---

## 기술 스택 (고칠 거 있으면 자유롭게 수정 좀)
[![stackticon](https://firebasestorage.googleapis.com/v0/b/stackticon-81399.appspot.com/o/images%2F1766413899094?alt=media&token=1e0dcb56-60c7-450b-9bb2-6c3be11fee7c)](https://github.com/msdio/stackticon)

---

## 주요 기능 (맡은 기능 추가 좀요...)

<details>
<summary><b>👤 회원 기능</b></summary>

- 로그인 / 소셜 로그인
- 회원가입 / 아이디 찾기 / 비밀번호 재설정
- 블로거 구독 결제
  - 프리미엄 기능(황금 키워드, 블로그 지수 등)을 위한 구독 결제
- ....

</details>

<details>
<summary><b>📊 대시보드</b></summary>

- 예시 A

</details>

<details>
<summary><b>📢 캠페인</b></summary>

**공통**
- 캠페인 리스트 조회 및 필터 검색
  - 카테고리, 조건, 마감일 등 다양한 필터 제공
  - 인기순, 최신순, 마감임박순 정렬
- 캠페인 상세 페이지 열람
  - 캠페인 개요, 미션 조건, 보상, 유의사항 등 확인 가능
    
**블로거**
- 캠페인 지원 / 취소
  - 캠페인 조건 확인 후 지원 가능
  - 지원 상태별 관리 및 취소 기능
- 채택 여부 및 후기 등록
  - 채택된 캠페인 후기 작성 및 제출
  - 후기 승인 여부 및 반려 사유 확인
- 나의 캠페인 관리
  - 진행 중 / 완료 등 상태별 캠페인 리스트 관리

**광고주**
- 광고주 ....

</details>

<details>
<summary><b>💬 1:1 채팅</b></summary>

- 블로거, 광고주 1:1 채팅 (WebSocket, Stomp 기반)
- ....

</details>

<details>
<summary><b>👨‍💼 관리자</b></summary>

- 회원 정보 수정
- 가입 승인/반려
- 블로그 주소 수동 인증
- 문의 답변
- 신고 처리
- 캠페인 승인/반려
- 캠페인 신청자/채택자 조회
- 캠페인 채택자 - 반려 기록 및 재제출 내용 조회
- 캠페인 카테고리 - 노출 순위 변경 / 추가 / 병합 / 분할

</details>

<details>
<summary><b>❓ 문의 / 신고 / 도움말</b></summary>

- 문의 / 신고글 작성 및 조회
- 도움말 조회

</details>

---

## 프로젝트 아키텍처
추가 예정
![Architecture](아키텍처_이미지_경로)

---

## ERD
<img width="2233" height="1160" alt="2차ERD" src="https://github.com/user-attachments/assets/7c60e939-41a1-4591-ad45-29765736864d" />


### 테이블 명세서

<details>
<summary><b>회원</b></summary>
<img width="898" height="850" alt="회원1" src="https://github.com/user-attachments/assets/5e4a0e58-4bef-415c-8fe2-91ab7eafd788" />
<img width="898" height="705" alt="회원2" src="https://github.com/user-attachments/assets/ddbf78d6-87b7-4995-81cd-28849586fe90" />


</details>

<details>
<summary><b>캠페인</b></summary>
<img width="1220" height="953" alt="캠페인1" src="https://github.com/user-attachments/assets/51c355f0-5381-492a-9397-a0a4eadeb628" />
<img width="1219" height="562" alt="캠페인2" src="https://github.com/user-attachments/assets/52d75725-6543-4251-8ed2-ca2ac1f26fd1" />


</details>

<details>
<summary><b>공통코드/지역</b></summary>
<img width="766" height="535" alt="공통코드" src="https://github.com/user-attachments/assets/0d98f649-8aac-4074-b311-10cbe2d8ea61" />

</details>

<details>
<summary><b>포스트 데이터</b></summary>
<img width="850" height="617" alt="포스트데이터" src="https://github.com/user-attachments/assets/06f6eb05-917d-46cb-85d8-42bcd3864fab" />


</details>

<details>
<summary><b>채팅</b></summary>
<img width="952" height="592" alt="채팅" src="https://github.com/user-attachments/assets/dcb931c3-770b-4779-a661-5a03c1829a9a" />


</details>

<details>
<summary><b>결제</b></summary>
<img width="876" height="460" alt="결제" src="https://github.com/user-attachments/assets/49dcf744-bdbe-4de1-b506-66b13cad1d27" />


</details>

<details>
<summary><b>문의/신고/도움말</b></summary>
<img width="950" height="795" alt="문의신고도움말1" src="https://github.com/user-attachments/assets/bf595520-cdbb-4b19-9e1a-67d405ce6ec9" />
<img width="1258" height="183" alt="ㅁㅜㄴㅇㅡㅣㅅㅣㄴㄱㅗㄷㅗㅇㅜㅁㅁㅏㄹ2" src="https://github.com/user-attachments/assets/5ea2a9d1-77fe-4fc4-aa77-73b85fd62642" />

</details>

<details>
<summary><b>첨부파일</b></summary>
<img width="1271" height="469" alt="첨부파일" src="https://github.com/user-attachments/assets/d7048761-afec-4375-81af-25a8701f68bb" />

</details>

---

## 팀원

| 이름 | GitHub |
|------|--------|
| 김동현 | [@dframe9801](https://github.com/dframe9801) |
| 김태현 | [@Hapakata](https://github.com/Hapakata) |
| 심석진 | [@sim748](https://github.com/sim748) |
| 이동은 | [@bbabpuli](https://github.com/bbabpuli) |
| 조성진 | [@choseongjin0815](https://github.com/choseongjin0815) |
| 최지우 | [@JIW0Ozip](https://github.com/JIW0Ozip) |
