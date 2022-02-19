package io.github.null2264.util;

public class WeightedBlock
{
    private String id;
    private Double weight;

    public WeightedBlock(String id, Double weight) {
        this.id = id;
        this.weight = weight;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getWeight() {
        return this.weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}