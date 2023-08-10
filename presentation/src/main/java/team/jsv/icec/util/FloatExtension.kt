package team.jsv.icec.util

/**
 * 프로젝트 전체 구조에서 신로되 값은 1 ~ 99 의 값을 받으므로 0.01 ~ 0.99 의 값을 반환합니다.
 * 이는 AI 모델의 신뢰도를 표현하는 값으로 사용됩니다.
 */

val Float.toThreshold: Float
    get() = this / 100f
