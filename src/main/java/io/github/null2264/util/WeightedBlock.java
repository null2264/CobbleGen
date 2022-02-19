package io.github.null2264.util;

public class WeightedBlock
{
    private String id;
    private Double weight;

    public WeightedBlock(String id, Double weight) {
        this.id = id;
        this.weight = weight;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWeight() {
        return this.weight;
    }
}