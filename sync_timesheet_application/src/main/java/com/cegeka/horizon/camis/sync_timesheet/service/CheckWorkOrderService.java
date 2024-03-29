package com.cegeka.horizon.camis.sync_timesheet.service;

import com.cegeka.horizon.camis.timesheet.Employee;
import com.cegeka.horizon.camis.timesheet.WorkOrderStart;
import com.cegeka.horizon.camis.workorder.WorkOrderAccess;
import com.cegeka.horizon.camis.workorder.WorkorderAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.groupingBy;

@Service
public class CheckWorkOrderService {

    private static final Logger logger = LoggerFactory.getLogger("CheckWorkOrders");

    @Autowired
    private WorkorderAccessService workorderAccessService;

    public void check(WebClient webClient, List<Employee> employees) {
        employees.forEach(
                employee -> employee.getFirstUseOfWorkOrders().stream()
                        .collect(groupingBy(WorkOrderStart::workOrder)).forEach(
                                (workOrder, workOrderStarts) -> {
                                    LocalDate start = workOrderStarts.stream().min(new WorkOrderStart.byStart()).get().start();
                                    WorkOrderAccess accessAllowed = workorderAccessService.isAccessAllowed(webClient, employee.resourceId(), workOrder,
                                            start);
                                    if(!accessAllowed.access()){
                                        logger.error("Access for {} for work order {} on date {} is NOT ALLOWED",
                                                employee.resourceId(), workOrder.value(), start.format(ofPattern("dd/MM/yyyy")));
                                    }else{
                                        logger.info("Access for {} for work order {} on date {} is ALLOWED",
                                                employee.resourceId(), workOrder.value(), start.format(ofPattern("dd/MM/yyyy")));
                                    }
                                }
                        )
            );
    }

}
