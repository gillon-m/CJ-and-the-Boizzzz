package scheduler;//####[1]####
//####[1]####
import java.awt.event.ActionEvent;//####[3]####
import java.awt.event.ActionListener;//####[4]####
import java.util.ArrayList;//####[5]####
import java.util.List;//####[6]####
import java.util.concurrent.ConcurrentLinkedQueue;//####[7]####
import java.util.concurrent.PriorityBlockingQueue;//####[8]####
import javax.swing.Timer;//####[10]####
import graph.Graph;//####[12]####
import graph.Vertex;//####[13]####
import gui.VisualiserController;//####[14]####
import gui.data.Data;
import heuristics.CostFunctionCalculator;//####[15]####
import pruning.ListScheduling;//####[16]####
import pruning.Pruning;//####[17]####
import components.ScheduleComparator;//####[18]####
//####[19]####
//-- ParaTask related imports//####[19]####
import pt.runtime.*;//####[19]####
import java.util.concurrent.ExecutionException;//####[19]####
import java.util.concurrent.locks.*;//####[19]####
import java.lang.reflect.*;//####[19]####
import pt.runtime.GuiThread;//####[19]####
import java.util.concurrent.BlockingQueue;//####[19]####
import java.util.ArrayList;//####[19]####
import java.util.List;//####[19]####
//####[19]####
/**
 * This Class uses the Schedule Class and Processor Class
 * to make schedules using the information from the Graph Variable which
 * contains all the list of vertex and edges.
 *
 * The schedules are created using A* algorithm.
 * This Class manages the created schedules and returns the optimal schedule.
 * 
 * If user selects to use parallelisation function, this class runs the specified 
 * number of threads in parallel for execution.
 *
 * @author Alex Yoo, CJ Bang
 *
 *///####[33]####
public class ParallelisedScheduler {//####[34]####
    static{ParaTask.init();}//####[34]####
    /*  ParaTask helper method to access private/protected slots *///####[34]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[34]####
        if (m.getParameterTypes().length == 0)//####[34]####
            m.invoke(instance);//####[34]####
        else if ((m.getParameterTypes().length == 1))//####[34]####
            m.invoke(instance, arg);//####[34]####
        else //####[34]####
            m.invoke(instance, arg, interResult);//####[34]####
    }//####[34]####
//####[35]####
    private int _numberOfProcessors;//####[35]####
//####[36]####
    private PriorityBlockingQueue<Schedule> _openSchedules;//####[36]####
//####[37]####
    private ConcurrentLinkedQueue<Schedule> _closedSchedules;//####[37]####
//####[38]####
    private List<Schedule> _partialExpanded;//####[38]####
//####[39]####
    private List<Schedule> _finalSchedule;//####[39]####
//####[40]####
    private int _upperBoundCost;//####[40]####
//####[41]####
    private int _numberOfCores;//####[41]####
//####[42]####
    private boolean _visualisation;//####[42]####
//####[43]####
    private Timer _timer;//####[43]####
//####[44]####
    private ActionListener action;//####[44]####
//####[45]####
    private int timerCount;//####[45]####
//####[46]####
    private VisualiserController _visualiserController;//####[46]####
//####[47]####
    private Data _data;//####[47]####
//####[49]####
    public ParallelisedScheduler(int numberOfProcessors, int numberOfCores, boolean visualisation) {//####[49]####
        _openSchedules = new PriorityBlockingQueue<Schedule>(Graph.getInstance().getVertices().size(), new ScheduleComparator());//####[50]####
        _closedSchedules = new ConcurrentLinkedQueue<Schedule>();//####[51]####
        _partialExpanded = new ArrayList<Schedule>();//####[52]####
        _finalSchedule = new ArrayList<Schedule>();//####[53]####
        _numberOfProcessors = numberOfProcessors;//####[54]####
        _numberOfCores = numberOfCores;//####[55]####
        ListScheduling ls = new ListScheduling(_numberOfProcessors);//####[56]####
        _upperBoundCost = ls.getUpperBoundCostFunction();//####[57]####
        _visualisation = visualisation;//####[58]####
        if (_visualisation) //####[59]####
        {//####[59]####
            _data = Data.getInstance();//####[60]####
            _visualiserController = new VisualiserController();//####[61]####
            setUpTimer();//####[62]####
        }//####[63]####
    }//####[64]####
//####[72]####
    /**
	 * Sets up a timer that goes off every 1 millisecond for GUI update.
	 * When the timer goes off, the current head of the open schedules list is passed to the GUI components
	 * so that GUI updates with the new changes. Data object stores the information about the update 
	 * and visualiser controller fires update call to update GUI.
	 *///####[72]####
    private void setUpTimer() {//####[72]####
        action = new ActionListener() {//####[72]####
//####[74]####
            public void actionPerformed(ActionEvent arg0) {//####[74]####
                if (timerCount == 0) //####[75]####
                {//####[75]####
                    Schedule schedule = _openSchedules.peek();//####[76]####
                    _data.setCurrentSchedule(schedule);//####[77]####
                    _data.updateTotalNumberOfCreatedSchedules(_openSchedules.size() + _closedSchedules.size());//####[78]####
                    _visualiserController.update(false);//####[79]####
                } else {//####[80]####
                    timerCount--;//####[81]####
                }//####[82]####
            }//####[83]####
        };//####[83]####
        _timer = new Timer(1, action);//####[85]####
        _timer.setInitialDelay(0);//####[86]####
        _timer.start();//####[87]####
    }//####[88]####
//####[95]####
    /**
	 * This method returns the optimal schedule
	 * @return void
	 * @throws Exception
	 *///####[95]####
    public Schedule getOptimalSchedule() {//####[95]####
        this.addRootVerticesSchedulesToOpenSchedule();//####[96]####
        Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();//####[97]####
        if (_visualisation) //####[98]####
        {//####[98]####
            _timer.stop();//####[99]####
            _data.setCurrentSchedule(optimalSchedule);//####[100]####
            _data.updateTotalNumberOfCreatedSchedules(_openSchedules.size() + _closedSchedules.size());//####[101]####
            _visualiserController.update(true);//####[102]####
        }//####[103]####
        return optimalSchedule;//####[104]####
    }//####[105]####
//####[114]####
    /**
	 * This method uses the A* algorithm to create schedules 
	 * It only returns back once it finds an optimal schedule
	 * It throws an exception if the openschedule queue is empty because that is not suppose to happen
	 * 
	 * @return optimal schedule
	 *///####[114]####
    private Schedule makeSchedulesUsingAlgorithm() {//####[114]####
        if (_numberOfCores == -1) //####[115]####
        {//####[115]####
            while (_finalSchedule.isEmpty()) //####[116]####
            {//####[116]####
                searchAndExpand();//####[117]####
            }//####[118]####
            return _finalSchedule.get(0);//####[119]####
        } else {//####[120]####
            while (_finalSchedule.isEmpty() && (_openSchedules.size() < _numberOfCores)) //####[121]####
            {//####[121]####
                searchAndExpand();//####[122]####
            }//####[123]####
            if (_finalSchedule.isEmpty()) //####[124]####
            {//####[124]####
                TaskIDGroup g = paralleliseSearch(_openSchedules);//####[125]####
                try {//####[126]####
                    g.waitTillFinished();//####[127]####
                } catch (ExecutionException e) {//####[128]####
                    e.printStackTrace();//####[129]####
                } catch (InterruptedException e) {//####[130]####
                    e.printStackTrace();//####[131]####
                }//####[132]####
            }//####[133]####
            return _finalSchedule.get(0);//####[134]####
        }//####[135]####
    }//####[136]####
//####[145]####
    /**
	 * Gets the head of the priority queue and expands it to the children schedules.
	 * If the optimal schedule is found, it stores it to the final schedule list.
	 * if not found, the children schedules get added to the open schedules list 
	 * if pass the conditions required.
	 * 
	 *///####[145]####
    private void searchAndExpand() {//####[145]####
        Schedule currentSchedule = _openSchedules.poll();//####[146]####
        _closedSchedules.add(currentSchedule);//####[147]####
        if (this.hasScheduleUsedAllPossibleVertices(currentSchedule)) //####[148]####
        {//####[148]####
            _finalSchedule.add(currentSchedule);//####[149]####
        }//####[150]####
        this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);//####[151]####
    }//####[152]####
//####[164]####
    private static volatile Method __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = null;//####[164]####
    private synchronized static void __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet() {//####[164]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[164]####
            try {//####[164]####
                __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__paralleliseSearch", new Class[] {//####[164]####
                    PriorityBlockingQueue.class//####[164]####
                });//####[164]####
            } catch (Exception e) {//####[164]####
                e.printStackTrace();//####[164]####
            }//####[164]####
        }//####[164]####
    }//####[164]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[164]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[164]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[164]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[164]####
    }//####[164]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[164]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules, TaskInfo taskinfo) {//####[164]####
        // ensure Method variable is set//####[164]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[164]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[164]####
        }//####[164]####
        taskinfo.setParameters(_openSchedules);//####[164]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[164]####
        taskinfo.setInstance(this);//####[164]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[164]####
    }//####[164]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[164]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[164]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[164]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[164]####
    }//####[164]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[164]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[164]####
        // ensure Method variable is set//####[164]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[164]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[164]####
        }//####[164]####
        taskinfo.setTaskIdArgIndexes(0);//####[164]####
        taskinfo.addDependsOn(_openSchedules);//####[164]####
        taskinfo.setParameters(_openSchedules);//####[164]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[164]####
        taskinfo.setInstance(this);//####[164]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[164]####
    }//####[164]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[164]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[164]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[164]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[164]####
    }//####[164]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[164]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[164]####
        // ensure Method variable is set//####[164]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[164]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[164]####
        }//####[164]####
        taskinfo.setQueueArgIndexes(0);//####[164]####
        taskinfo.setIsPipeline(true);//####[164]####
        taskinfo.setParameters(_openSchedules);//####[164]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[164]####
        taskinfo.setInstance(this);//####[164]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[164]####
    }//####[164]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[164]####
    public void __pt__paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[164]####
        while (_finalSchedule.isEmpty()) //####[165]####
        {//####[165]####
            searchAndExpand();//####[166]####
        }//####[167]####
    }//####[168]####
//####[168]####
//####[178]####
    /**
	 * For the current schedule we are processing,
	 * it tries to find successor schedules that are available
	 * Those successors schedules are then added to the open schedule if it
	 * passes the conditions required 
	 *
	 * @param currentSchedule
	 *///####[178]####
    private void addCurrentSchedulePossibleSuccessorsToOpenSchedule(Schedule currentSchedule) {//####[178]####
        List<Vertex> currentVertexSuccessors = currentSchedule.getChildVertices();//####[179]####
        for (Vertex childVertex : currentVertexSuccessors) //####[180]####
        {//####[180]####
            Schedule currentScheduleCopy = new Schedule(currentSchedule);//####[182]####
            Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];//####[183]####
            currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);//####[185]####
            CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();//####[187]####
            int parentScheduleCost = costFunctionCalculator.getTotalCostFunction(currentSchedule);//####[188]####
            if (_partialExpanded.contains(currentSchedule)) //####[190]####
            {//####[190]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[191]####
                {//####[191]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[192]####
                    if (childScheduleCost <= _upperBoundCost) //####[194]####
                    {//####[194]####
                        if (parentScheduleCost > costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i])) //####[195]####
                        {//####[195]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[196]####
                            {//####[196]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[197]####
                            }//####[198]####
                        }//####[199]####
                    }//####[200]####
                }//####[201]####
            } else {//####[202]####
                boolean partialExpanded = false;//####[203]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[204]####
                {//####[204]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[205]####
                    if (parentScheduleCost >= childScheduleCost) //####[206]####
                    {//####[206]####
                        if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[207]####
                        {//####[207]####
                            _openSchedules.add(currentChildVertexSchedules[i]);//####[208]####
                            partialExpanded = true;//####[209]####
                        }//####[210]####
                    }//####[211]####
                }//####[212]####
                if (!partialExpanded) //####[213]####
                {//####[213]####
                    for (int i = 0; i < _numberOfProcessors; i++) //####[214]####
                    {//####[214]####
                        int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[215]####
                        if (childScheduleCost <= _upperBoundCost) //####[217]####
                        {//####[217]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[218]####
                            {//####[218]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[219]####
                            }//####[220]####
                        }//####[221]####
                    }//####[222]####
                } else {//####[223]####
                    _openSchedules.add(currentSchedule);//####[224]####
                    _closedSchedules.remove(currentSchedule);//####[225]####
                    _partialExpanded.add(currentSchedule);//####[226]####
                }//####[227]####
            }//####[228]####
        }//####[229]####
    }//####[230]####
//####[244]####
    /**
	 * This method checks if the successor schedules have the conditions required to
	 * get added into the openschedule.
	 * It also checks inside the openschedule and closedschedule if there are any schedules that can be taken out
	 * that was made redundant by predecessor schedules
	 *
	 * returns true if it passes
	 * otherwise returns false
	 *
	 * @param childSchedule
	 * @return
	 *///####[244]####
    private boolean checkScheduleThroughPruning(Schedule childSchedule) {//####[244]####
        Pruning pruning = new Pruning();//####[245]####
        if (pruning.isCurrentScheduleNeeded(_openSchedules, _closedSchedules, childSchedule)) //####[246]####
        {//####[246]####
            return true;//####[247]####
        }//####[248]####
        return false;//####[249]####
    }//####[251]####
//####[261]####
    /**
	 * This method checks if the current schedule is a finished schedule
	 *
	 * returns true if it is
	 * otherwise returns false
	 *
	 * @param currentSchedule
	 * @return
	 *///####[261]####
    private boolean hasScheduleUsedAllPossibleVertices(Schedule currentSchedule) {//####[261]####
        List<Vertex> currentScheduleUsedVertices = currentSchedule.getAllUsedVertices();//####[262]####
        for (Vertex vertex : Graph.getInstance().getVertices()) //####[263]####
        {//####[263]####
            if (!currentScheduleUsedVertices.contains(vertex)) //####[264]####
            {//####[264]####
                return false;//####[265]####
            }//####[266]####
        }//####[267]####
        return true;//####[268]####
    }//####[269]####
//####[278]####
    /**
	 * This method adds root schedules to openschedules
	 * since at the start of schedule the first _timerTask is the same no matter which processor
	 * it is put on so only one variation of root schedule is added.
	 *
	 *///####[278]####
    private void addRootVerticesSchedulesToOpenSchedule() {//####[278]####
        for (Vertex rootVertex : Graph.getInstance().getRootVertices()) //####[279]####
        {//####[279]####
            Schedule emptySchedule = new Schedule(_numberOfProcessors);//####[280]####
            Schedule[] rootSchedules = new Schedule[_numberOfProcessors];//####[281]####
            rootSchedules = emptySchedule.generateAllPossibleScheduleForSpecifiedVertex(rootVertex);//####[283]####
            _openSchedules.add(rootSchedules[0]);//####[285]####
        }//####[286]####
    }//####[287]####
}//####[287]####
