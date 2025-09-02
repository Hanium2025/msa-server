package hanium.product_service.domain;

import lombok.Getter;

@Getter
public enum ReportReason {
    ILLEGAL("불법 거래"),
    ABUSE("욕설/인신공격 포함"),
    INFO_EXPOSURE("개인정보 노출"),
    OBSCENITY("음란성/선전성"),
    FRAUD("사기 거래 이력"),
    OTHER("기타"),
    ;

    private final String label;

    ReportReason(String label) {
        this.label = label;
    }
}
