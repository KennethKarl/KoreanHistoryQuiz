package com.historyquiz.app.data.local.db

import com.historyquiz.app.data.local.dao.QuestionDao
import com.historyquiz.app.data.local.entity.QuestionEntity
import com.google.gson.Gson

/**
 * 앱 첫 실행 시 Room DB에 한국사 기본 문제를 삽입한다.
 * 이미 문제가 있으면 삽입하지 않는다.
 */
class SeedDataHelper(private val questionDao: QuestionDao) {

    private val gson = Gson()

    private fun opts(vararg items: String): String = gson.toJson(items.toList())

    suspend fun seedIfEmpty() {
        val basicCount = questionDao.countByLevel("basic")
        val advancedCount = questionDao.countByLevel("advanced")

        if (basicCount == 0) {
            questionDao.insertAll(basicSeedQuestions())
        }
        if (advancedCount == 0) {
            questionDao.insertAll(advancedSeedQuestions())
        }
    }

    private fun basicSeedQuestions(): List<QuestionEntity> = listOf(
        QuestionEntity(
            id = "seed_basic_001",
            content = "고조선을 건국한 인물은?",
            options = opts("단군왕검", "주몽", "온조", "박혁거세"),
            answerIndex = 0,
            level = "basic",
            category = "고조선"
        ),
        QuestionEntity(
            id = "seed_basic_002",
            content = "삼국시대에 가장 먼저 건국된 나라는?",
            options = opts("신라", "백제", "고구려", "가야"),
            answerIndex = 2,
            level = "basic",
            category = "삼국시대"
        ),
        QuestionEntity(
            id = "seed_basic_003",
            content = "고려를 건국한 인물은?",
            options = opts("왕건", "견훤", "궁예", "이성계"),
            answerIndex = 0,
            level = "basic",
            category = "고려"
        ),
        QuestionEntity(
            id = "seed_basic_004",
            content = "조선을 건국한 인물은?",
            options = opts("왕건", "이성계", "정도전", "이방원"),
            answerIndex = 1,
            level = "basic",
            category = "조선"
        ),
        QuestionEntity(
            id = "seed_basic_005",
            content = "한글(훈민정음)을 창제한 왕은?",
            options = opts("태조", "태종", "세종대왕", "성종"),
            answerIndex = 2,
            level = "basic",
            category = "조선"
        ),
        QuestionEntity(
            id = "seed_basic_006",
            content = "임진왜란이 시작된 연도는?",
            options = opts("1592년", "1597년", "1636년", "1627년"),
            answerIndex = 0,
            level = "basic",
            category = "조선"
        ),
        QuestionEntity(
            id = "seed_basic_007",
            content = "3.1 운동이 일어난 연도는?",
            options = opts("1910년", "1919년", "1945년", "1948년"),
            answerIndex = 1,
            level = "basic",
            category = "일제강점기"
        ),
        QuestionEntity(
            id = "seed_basic_008",
            content = "대한민국 초대 대통령은?",
            options = opts("김구", "이승만", "박정희", "윤보선"),
            answerIndex = 1,
            level = "basic",
            category = "대한민국"
        ),
        QuestionEntity(
            id = "seed_basic_009",
            content = "신라가 삼국을 통일한 시기는?",
            options = opts("562년", "660년", "668년", "676년"),
            answerIndex = 3,
            level = "basic",
            category = "삼국시대"
        ),
        QuestionEntity(
            id = "seed_basic_010",
            content = "고구려를 건국한 인물은?",
            options = opts("온조", "박혁거세", "주몽", "김수로"),
            answerIndex = 2,
            level = "basic",
            category = "삼국시대"
        )
    )

    private fun advancedSeedQuestions(): List<QuestionEntity> = listOf(
        QuestionEntity(
            id = "seed_adv_001",
            content = "고려의 삼별초 항쟁에 대한 설명으로 옳은 것은?",
            options = opts(
                "무신 정권에 대항하여 봉기하였다",
                "몽골 강화에 반대하여 항쟁을 이어갔다",
                "조선 건국에 반대한 세력이 주도하였다",
                "왜구 침입에 대항하여 조직된 군대이다"
            ),
            answerIndex = 1,
            level = "advanced",
            category = "고려"
        ),
        QuestionEntity(
            id = "seed_adv_002",
            content = "1884년 갑신정변을 주도한 인물로 옳은 것은?",
            options = opts("김옥균", "흥선대원군", "최익현", "안중근"),
            answerIndex = 0,
            level = "advanced",
            category = "조선"
        ),
        QuestionEntity(
            id = "seed_adv_003",
            content = "을사늑약(을사조약)이 체결된 연도는?",
            options = opts("1894년", "1900년", "1905년", "1910년"),
            answerIndex = 2,
            level = "advanced",
            category = "일제강점기"
        ),
        QuestionEntity(
            id = "seed_adv_004",
            content = "독립운동가 안중근이 하얼빈에서 저격한 인물은?",
            options = opts("이토 히로부미", "야마가타 아리토모", "데라우치 마사타케", "하세가와 요시미치"),
            answerIndex = 0,
            level = "advanced",
            category = "일제강점기"
        ),
        QuestionEntity(
            id = "seed_adv_005",
            content = "고려 팔만대장경을 제작한 주요 목적은?",
            options = opts(
                "불교 포교를 위해",
                "몽골 침략 격퇴를 기원하기 위해",
                "왕권 강화를 위해",
                "천문 관측 기록을 위해"
            ),
            answerIndex = 1,
            level = "advanced",
            category = "고려"
        ),
        QuestionEntity(
            id = "seed_adv_006",
            content = "조선의 기본 법전인 경국대전이 완성된 시기는?",
            options = opts("세종 때", "세조 때", "성종 때", "태종 때"),
            answerIndex = 2,
            level = "advanced",
            category = "조선"
        ),
        QuestionEntity(
            id = "seed_adv_007",
            content = "병자호란이 일어난 연도는?",
            options = opts("1592년", "1627년", "1636년", "1644년"),
            answerIndex = 2,
            level = "advanced",
            category = "조선"
        ),
        QuestionEntity(
            id = "seed_adv_008",
            content = "한일병합조약이 체결된 연도는?",
            options = opts("1905년", "1907년", "1910년", "1919년"),
            answerIndex = 2,
            level = "advanced",
            category = "일제강점기"
        ),
        QuestionEntity(
            id = "seed_adv_009",
            content = "대한민국 제헌 국회가 구성된 연도는?",
            options = opts("1945년", "1946년", "1948년", "1950년"),
            answerIndex = 2,
            level = "advanced",
            category = "대한민국"
        ),
        QuestionEntity(
            id = "seed_adv_010",
            content = "6·25 전쟁이 발발한 날짜는?",
            options = opts("1950년 6월 25일", "1950년 9월 15일", "1951년 6월 25일", "1953년 7월 27일"),
            answerIndex = 0,
            level = "advanced",
            category = "대한민국"
        )
    )
}
