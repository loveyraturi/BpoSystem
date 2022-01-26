package com.praveen.model;

public class LeadVersionCountAndData {

  private LeadVersions leads;
  private Long count;

  public LeadVersionCountAndData(LeadVersions leads, Long count) {
    this.leads = leads;
    this.count  = count;
  }
}