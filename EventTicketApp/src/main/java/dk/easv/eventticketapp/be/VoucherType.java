package dk.easv.eventticketapp.be;

import dk.easv.eventticketapp.be.enums.DiscountType;
import dk.easv.eventticketapp.be.enums.VoucherStatus;

public class VoucherType {
    private int id;
    private String name;
    private String description;
    private double discountValue;
    private DiscountType discountType;

    public VoucherType(int id, String name, String description, double discountValue, DiscountType discountType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.discountValue = discountValue;
        this.discountType = discountType;
    }


    public int getId() {return id;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public double getDiscountValue() {return discountValue;}
    public DiscountType getDiscountType() {return discountType;}

    //only for DAO inserting
    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setDescription(String description) {this.description = description;}
    public void setDiscountValue(double discountValue) {this.discountValue = discountValue;}
    public void setDiscountType(DiscountType discountType) {this.discountType = discountType;}
    @Override
    public String toString() {
        return name;
    }



}
