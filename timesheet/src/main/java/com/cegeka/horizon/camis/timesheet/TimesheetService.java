package com.cegeka.horizon.camis.timesheet;

import com.cegeka.horizon.camis.domain.ResourceId;
import com.cegeka.horizon.camis.domain.WorkOrder;
import org.threeten.extra.LocalDateRange;

import java.time.LocalDate;

public interface TimesheetService {
    Employee getTimesheetEntries(ResourceId resourceId, String employeeName, LocalDateRange dateRange);

    TimesheetLineIdentifier createTimesheetEntry(ResourceId resourceId, WorkOrder workOrder, LoggedHoursByDay loggedHoursByDay);

    TimesheetLineIdentifier updateTimesheetEntry(TimesheetLineIdentifier timesheetLineIdentifier, ResourceId resourceId, WorkOrder workOrder, LoggedHoursByDay loggedHours);

    boolean deleteTimesheetEntry(TimesheetLineIdentifier timesheetLineIdentifier, ResourceId resourceId);
}
