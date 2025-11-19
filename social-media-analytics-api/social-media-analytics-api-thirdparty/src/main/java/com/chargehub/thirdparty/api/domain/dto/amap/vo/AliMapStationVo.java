package com.chargehub.thirdparty.api.domain.dto.amap.vo;

import com.chargehub.thirdparty.api.domain.dto.amap.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Zhanghaowei
 * @date 2024/07/30 13:36
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class AliMapStationVo implements Serializable {
    private static final long serialVersionUID = 4261802825690733684L;

    @JsonProperty("StationID")
    private String stationId;

    @JsonProperty("OperatorID")
    private String operatorId;

    @JsonProperty("EquipmentOwnerID")
    private String equipmentOwnerId;

    @JsonProperty("OperatorName")
    private String operatorName;

    @JsonProperty("StationName")
    private String stationName;

    @JsonProperty("CountryCode")
    private String countryCode;

    @JsonProperty("AreaCode")
    private String areaCode;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("StationTel")
    private String stationTel;

    @JsonProperty("ServiceTel")
    private String serviceTel;

    @JsonProperty("StationType")
    private Integer stationType;

    @JsonProperty("StationStatus")
    private Integer stationStatus;

    @JsonProperty("OpenType")
    private Integer openType;

    @JsonProperty("ParkNums")
    private Integer parkNums;

    @JsonProperty("StationLng")
    private BigDecimal stationLng;

    @JsonProperty("StationLat")
    private BigDecimal stationLat;

    @JsonProperty("Construction")
    private Integer construction;

    @JsonProperty("Pictures")
    private List<AliMapPicture> pictures;

    @JsonProperty("MatchCars")
    private String matchCars;

    @JsonProperty("ParkInfo")
    private String parkInfo;

    @JsonProperty("BusineHours")
    private String busineHours;

    @JsonProperty("PriceChargingInfo")
    private List<AliPriceChargingInfo> priceChargingInfo;

    @JsonProperty("DiscountPriceChargingInfo")
    private List<AliDiscountPriceChargingInfo> discountPriceChargingInfo;

    @JsonProperty("ParkFee")
    private String parkFee;

    @JsonProperty("Payment")
    private String payment;

    @JsonProperty("SupportOrder")
    private Integer supportOrder;

    @JsonProperty("SuperEquipmentNum")
    private Integer superEquipmentNum;

    @JsonProperty("FastEquipmentNum")
    private Integer fastEquipmentNum;

    @JsonProperty("SlowEquipmentNum")
    private Integer slowEquipmentNum;

    @JsonProperty("Remark")
    private String remark;

    @JsonProperty("EquipmentInfos")
    private List<AliEquipmentInfo> equipmentInfos;

    @JsonProperty("RightTag")
    private String rightTag;

    @JsonProperty("RoadBook")
    private List<AliRoadBook> roadBook;

    @JsonProperty("SuperPriceChargingInfo")
    private List<AliPriceChargingInfo> superPriceChargingInfo;

    @JsonProperty("SuperDiscountPriceChargingInfo")
    private List<AliDiscountPriceChargingInfo> superDiscountPriceChargingInfo;

    @JsonProperty("FastPriceChargingInfo")
    private List<AliPriceChargingInfo> fastPriceChargingInfo;

    @JsonProperty("FastDiscountPriceChargingInfo")
    private List<AliDiscountPriceChargingInfo> fastDiscountPriceChargingInfo;

    @JsonProperty("SlowPriceChargingInfo")
    private List<AliPriceChargingInfo> slowPriceChargingInfo;

    @JsonProperty("SlowDiscountPriceChargingInfo")
    private List<AliDiscountPriceChargingInfo> slowDiscountPriceChargingInfo;

    @JsonProperty("StationFacility")
    private String stationFacility;

    @JsonProperty("ChargingType")
    private Integer chargingType;

    @JsonProperty("ProviderId")
    private String providerId;

    @JsonProperty("GreenEnergy")
    private Boolean greenEnergy;

    @JsonProperty("PlugAndCharge")
    private String plugAndCharge;

    @JsonProperty("ParkPolicy")
    private Integer parkPolicy;

    @JsonProperty("BookType")
    private Integer bookType;

    @JsonProperty("SupportSale")
    private Boolean supportSale;


}
