package com.cegeka.horizon.camis.synctimesheet.service;

import com.cegeka.horizon.camis.synctimesheet.service.command.CreateTimesheetEntryCommand;
import com.cegeka.horizon.camis.synctimesheet.service.command.ErrorCommand;
import com.cegeka.horizon.camis.synctimesheet.service.command.NothingToSyncCommand;
import com.cegeka.horizon.camis.timesheet.Employee;
import org.junit.jupiter.api.Test;

import static com.cegeka.horizon.camis.timesheet.testbuilder.EmployeeTestBuilder.anEmployee;
import static com.cegeka.horizon.camis.timesheet.testbuilder.LoggedHoursByDayTestBuilder.aLoggedHours;
import static com.cegeka.horizon.camis.timesheet.testbuilder.TestLocalDates.*;
import static com.cegeka.horizon.camis.timesheet.testbuilder.TestWorkOrders.WORK_ORDER_1;
import static com.cegeka.horizon.camis.timesheet.testbuilder.TestWorkOrders.WORK_ORDER_2;
import static com.cegeka.horizon.camis.timesheet.testbuilder.TimesheetLineTestBuilder.aTimesheetLine;
import static com.cegeka.horizon.camis.timesheet.testbuilder.WeeklyTimesheetTestBuilder.aWeeklyTimesheet;
import static org.assertj.core.api.Assertions.assertThat;

class CompareEmployeeServiceTest {

    @Test
    public void givenExactTimesheetLine_whenCompare_NoAction(){
        Employee inputEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(aLoggedHours(6, WEEK_0_MONDAY)))
                ).build();

        Employee camisEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(aLoggedHours(6, WEEK_0_MONDAY)))
                ).build();

        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee)).hasSize(1);
        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(0)).isInstanceOf(NothingToSyncCommand.class);
    }

    @Test
    public void givenMoreHoursTimesheetLine_whenCompare_ErrorCommand(){
        Employee inputEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(
                                aLoggedHours(6, WEEK_0_MONDAY)))
                ).build();

        Employee camisEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(
                                aLoggedHours(8, WEEK_0_MONDAY)))
                ).build();

        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee)).hasSize(1);
        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(0)).isInstanceOf(ErrorCommand.class);
    }

    @Test
    public void giveIncompleteTimesheetLineWithMissingDay_whenCompare_AddCommand(){
        Employee inputEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(
                                    aLoggedHours(6, WEEK_0_MONDAY),
                                    aLoggedHours(8, WEEK_0_TUESDAY)))
                ).build();

        Employee camisEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(
                                    aLoggedHours(6, WEEK_0_MONDAY)))
                ).build();

        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee)).hasSize(2);
        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(0)).isInstanceOf(NothingToSyncCommand.class);
        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(1)).isInstanceOf(CreateTimesheetEntryCommand.class);
        CreateTimesheetEntryCommand createTimesheetEntryCommand = (CreateTimesheetEntryCommand) new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(1);
        assertThat(createTimesheetEntryCommand.loggedHours()).isEqualTo(aLoggedHours(8, WEEK_0_TUESDAY).build());
    }

    @Test
    public void givenIncompleteTimesheetLineWithMissingDay_whenCompare_AddCommand(){
        Employee inputEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(
                                aLoggedHours(6, WEEK_0_MONDAY),
                                aLoggedHours(8, WEEK_0_TUESDAY)))
                ).build();

        Employee camisEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(
                                aLoggedHours(6, WEEK_0_MONDAY)))
                ).build();

        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee)).hasSize(2);
        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(0)).isInstanceOf(NothingToSyncCommand.class);
        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(1)).isInstanceOf(CreateTimesheetEntryCommand.class);
        CreateTimesheetEntryCommand createTimesheetEntryCommand = (CreateTimesheetEntryCommand) new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(1);
        assertThat(createTimesheetEntryCommand.loggedHours()).isEqualTo(aLoggedHours(8, WEEK_0_TUESDAY).build());
    }

    @Test
    public void givenIncompleteTimesheetLineWithMissingHoursOnDay_whenCompare_AddCommand(){
        Employee inputEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(aLoggedHours(8, WEEK_0_MONDAY)))
                ).build();

        Employee camisEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(aLoggedHours(6, WEEK_0_MONDAY)))
                ).build();

        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee)).hasSize(1);
        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(0)).isInstanceOf(CreateTimesheetEntryCommand.class);
        CreateTimesheetEntryCommand createTimesheetEntryCommand = (CreateTimesheetEntryCommand) new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(0);
        assertThat(createTimesheetEntryCommand.loggedHours()).isEqualTo(aLoggedHours(2, WEEK_0_MONDAY).build());
    }

    @Test
    public void givenIncompleteTimesheetLineWithMissingHoursOnDayDifferentWorkOrder_whenCompare_AddCommand(){
        Employee inputEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(aLoggedHours(7, WEEK_0_MONDAY)),
                                  aTimesheetLine().withWorkOrder(WORK_ORDER_2).withLoggedHours(aLoggedHours(3, WEEK_0_MONDAY)))
                ).build();

        Employee camisEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1).withLoggedHours(aLoggedHours(6, WEEK_0_MONDAY)))
                ).build();

        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee)).hasSize(2);
        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(0)).isInstanceOf(CreateTimesheetEntryCommand.class);
        CreateTimesheetEntryCommand createTimesheetEntryCommand = (CreateTimesheetEntryCommand) new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(0);
        assertThat(createTimesheetEntryCommand.loggedHours()).isEqualTo(aLoggedHours(1, WEEK_0_MONDAY).build());
        assertThat(createTimesheetEntryCommand.workOrder()).isEqualTo(WORK_ORDER_1);

        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(1)).isInstanceOf(CreateTimesheetEntryCommand.class);
        createTimesheetEntryCommand = (CreateTimesheetEntryCommand) new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(1);
        assertThat(createTimesheetEntryCommand.loggedHours()).isEqualTo(aLoggedHours(3, WEEK_0_MONDAY).build());
        assertThat(createTimesheetEntryCommand.workOrder()).isEqualTo(WORK_ORDER_2);
    }

    @Test
    public void givenUnexistingTimesheetLine_whenCompare_AddCommand(){
        Employee inputEmployee = anEmployee()
                .withTimeSheet(aWeeklyTimesheet()
                        .withLine(aTimesheetLine().withWorkOrder(WORK_ORDER_1)
                                    .withLoggedHours(
                                            aLoggedHours(6, WEEK_0_MONDAY),
                                            aLoggedHours(8, WEEK_0_WEDNESDAY)))
                ).build();

        Employee camisEmployee = anEmployee()
                                .build();

        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee)).hasSize(2);
        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(0)).isInstanceOf(CreateTimesheetEntryCommand.class);
        CreateTimesheetEntryCommand createTimesheetEntryCommand = (CreateTimesheetEntryCommand) new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(0);
        assertThat(createTimesheetEntryCommand.loggedHours()).isEqualTo(aLoggedHours(6, WEEK_0_MONDAY).build());

        assertThat(new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(1)).isInstanceOf(CreateTimesheetEntryCommand.class);
        createTimesheetEntryCommand = (CreateTimesheetEntryCommand) new CompareEmployeeService().compare(inputEmployee, camisEmployee).get(1);
        assertThat(createTimesheetEntryCommand.loggedHours()).isEqualTo(aLoggedHours(8, WEEK_0_WEDNESDAY).build());
    }

}