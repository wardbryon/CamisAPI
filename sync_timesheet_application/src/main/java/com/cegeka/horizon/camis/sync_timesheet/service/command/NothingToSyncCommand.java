package com.cegeka.horizon.camis.sync_timesheet.service.command;

import com.cegeka.horizon.camis.domain.EmployeeIdentification;
import com.cegeka.horizon.camis.domain.WorkOrder;
import com.cegeka.horizon.camis.sync.logger.model.result.CamisWorkorderInfo;
import com.cegeka.horizon.camis.sync.logger.model.result.SyncResult;
import com.cegeka.horizon.camis.timesheet.TimesheetService;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

public class NothingToSyncCommand implements SyncCommand {
    private final EmployeeIdentification employeeId;
    private final WorkOrder workOrder;
    private final LocalDate date;
    private final double hours;

    public NothingToSyncCommand(EmployeeIdentification employerData, WorkOrder workOrder, LocalDate date, double hours) {
        this.employeeId = employerData;
        this.workOrder = workOrder;
        this.date = date;
        this.hours = hours;
    }

    @Override
    public SyncResult execute(WebClient webClient, TimesheetService timesheetService) {
        return SyncResult.noActionSyncCorrect(employeeId, new CamisWorkorderInfo(date,String.format("No sync necessary for %s on work order %s and date %s, \n hours of input & Camis are already equal",employeeId.resourceId(), workOrder.value(), date), workOrder), hours);
    }
}
