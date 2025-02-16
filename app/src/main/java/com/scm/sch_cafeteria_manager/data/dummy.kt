package com.scm.sch_cafeteria_manager.data

object dummy {

    val tmDummy: TodayMenu = getDummyTMData()
    val dDummy: DetailMenu = getDummyDData()

    var tmMethod: TodayMenu? = null
        private set

    var dMethod: DetailMenu? = null
        private set

    private fun getDummyTMData(): TodayMenu {
        return TodayMenu(
            "2025-02-01",
            listOf(
                Cafeteria(
                    "향설1",
                    listOf(
                        Meal(
                            "조식",
                            "08:00",
                            "09:30",
                            "된장찌개, 비빔밥, 도시락",
                            "김치, 감자볶음"
                        ),
                        Meal(
                            "중식",
                            "11:00",
                            "14:00",
                            "불고기, 된장찌개",
                            "샐러드, 계란찜"
                        ),
                        Meal(
                            "석식",
                            "17:00",
                            "19:30",
                            "김치찌개, 계란말이",
                            "오이무침, 나물"
                        )
                    )
                ),
                Cafeteria(
                    "교직원 식당",
                    listOf(
                        Meal(
                            "중식",
                            "11:00",
                            "14:00",
                            "닭볶음탕, 잡채",
                            "나물, 깍두기"
                        )
                    )
                )
            )
        )
    }

    private fun getDummyDData(): DetailMenu {
        return DetailMenu(
            "향설1",
            "2025-02-01",
            "08:00",
            "20:00",
            true,
            Week(
                listOf(
                    Daily(
                        "월요일",
                        listOf(
                            Meal(
                                "조식",
                                "08:00",
                                "09:30",
                                "비엔나야채볶음, 비빔밥, 도시락",
                                "김치, 감자볶음"
                            ),
                            Meal(
                                "중식",
                                "11:00",
                                "14:00",
                                "불고기, 된장찌개",
                                "샐러드, 계란찜"
                            ),
                            Meal(
                                "석식",
                                "17:00",
                                "19:30",
                                "김치찌개, 계란말이",
                                "오이무침, 나물"
                            )
                        )
                    ),
                    Daily(
                        "화요일",
                        listOf(
                            Meal(
                                "조식",
                                "08:00",
                                "09:30",
                                "된장찌개, 비빔밥, 도시락",
                                "김치, 감자볶음"
                            ),
                            Meal(
                                "중식",
                                "11:00",
                                "14:00",
                                "불고기, 된장찌개",
                                "샐러드, 계란찜"
                            ),
                            Meal(
                                "석식",
                                "17:00",
                                "19:30",
                                "김치찌개, 계란말이",
                                "오이무침, 나물"
                            )
                        )
                    ),
                    Daily(
                        "수요일",
                        listOf(
                            Meal(
                                "조식",
                                "08:00",
                                "09:30",
                                "된장찌개, 비빔밥, 도시락",
                                "김치, 감자볶음"
                            ),
                            Meal(
                                "중식",
                                "11:00",
                                "14:00",
                                "불고기, 된장찌개",
                                "샐러드, 계란찜"
                            ),
                            Meal(
                                "석식",
                                "17:00",
                                "19:30",
                                "김치찌개, 계란말이",
                                "오이무침, 나물"
                            )
                        )
                    ),
                    Daily(
                        "목요일",
                        listOf(
                            Meal(
                                "조식",
                                "08:00",
                                "09:30",
                                "된장찌개, 비빔밥, 도시락",
                                "김치, 감자볶음"
                            ),
                            Meal(
                                "중식",
                                "11:00",
                                "14:00",
                                "불고기, 된장찌개",
                                "샐러드, 계란찜"
                            ),
                            Meal(
                                "석식",
                                "17:00",
                                "19:30",
                                "김치찌개, 계란말이",
                                "오이무침, 나물"
                            )
                        )

                    )
                )
            )
        )
    }
}

