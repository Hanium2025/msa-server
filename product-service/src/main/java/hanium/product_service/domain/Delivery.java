package hanium.product_service.domain;

import hanium.product_service.dto.request.CreateWayBillRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    private Trade trade;

    /*
     * 택배사 코드
     * 스마트 택배 API 호출 용도
     * 01 : 우체국 택배
     * 04 : CJ 대한통운
     * 05 : 한진택배
     * 06 : 로젠택배
     * 08 : 롯데택배
     * 24 : GS Postbox 택배
     * 46 : CU 편의점택배
     */
    @Column(name = "delivery_code")
    private String code;

    @Column(name = "invoice_number")
    private String invoiceNo;

    public static Delivery of(Trade trade, CreateWayBillRequestDTO dto){
        return Delivery.builder()
                .trade(trade)
                .code(dto.getCode())
                .invoiceNo(dto.getInvoiceNo())
                .build();
    }
}
