package com.example.clientbackend.controllers;

import com.example.clientbackend.organization.Organization;
import com.example.clientbackend.organization.OrganizationService;
import com.example.clientbackend.requests.OrganizationRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/organization_management")
@AllArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;


    @PostMapping(path = "create_organization")
    public void createOrganization(@RequestBody OrganizationRequest request) {
        organizationService.createOrganization(request);
    }

    @GetMapping(path = "organization_by_id")
    public Organization getOrganizationById(@RequestParam("id") long id) {
        return organizationService.getOrganizationById(id);
    }

    @PutMapping(path = "update_organization_by_id")
    public void updateOrganizationById(@RequestParam("id") long id, @RequestBody OrganizationRequest request) {
        organizationService.updateOrganization(id, request);
    }

    @DeleteMapping(path = "delete_organization_by_id")
    public void deleteOrganizationById(@RequestParam("id") long id) {
        organizationService.deleteOrganization(id);
    }
}
