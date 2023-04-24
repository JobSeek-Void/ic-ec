package team.jsv.icec.ui.camera

enum class SettingRatio(val id: Int) {
    RATIO_1_1(1),
    RATIO_3_4(2),
    RATIO_9_16(3),
    RATIO_FULL(4);

    companion object {
        const val AMOUNT_1_1 = 1.0
        const val AMOUNT_3_4 = 4.0 / 3.0
        const val AMOUNT_9_16 = 16.0 / 9.0
    }
}
