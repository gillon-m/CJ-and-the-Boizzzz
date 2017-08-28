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
import heuristics.CostFunctionCalculator;//####[15]####
import pruning.ListScheduling;//####[16]####
import pruning.Pruning;//####[17]####
import components.ScheduleComparator;//####[18]####
import gui.data.Data;//####[19]####
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
    private PriorityBlockingQueue<Schedule> _finalSchedules;//####[39]####
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
        _numberOfProcessors = numberOfProcessors;//####[50]####
        _numberOfCores = numberOfCores;//####[51]####
        _visualisation = visualisation;//####[52]####
        ScheduleComparator comparator = new ScheduleComparator();//####[53]####
        _openSchedules = new PriorityBlockingQueue<Schedule>(Graph.getInstance().getVertices().size(), comparator);//####[54]####
        _closedSchedules = new ConcurrentLinkedQueue<Schedule>();//####[55]####
        _partialExpanded = new ArrayList<Schedule>();//####[56]####
        ListScheduling ls = new ListScheduling(_numberOfProcessors);//####[57]####
        _upperBoundCost = ls.getUpperBoundCostFunction();//####[58]####
        _finalSchedules = new PriorityBlockingQueue<Schedule>(1, comparator);//####[59]####
        if (_visualisation) //####[60]####
        {//####[60]####
            _data = Data.getInstance();//####[61]####
            _visualiserController = new VisualiserController();//####[62]####
            setUpTimer();//####[63]####
        }//####[64]####
    }//####[65]####
//####[73]####
    /**
	 * Sets up a timer that goes off every 1 millisecond for GUI update.
	 * When the timer goes off, the current head of the open schedules list is passed to the GUI components
	 * so that GUI updates with the new changes. Data object stores the information about the update 
	 * and visualiser controller fires update call to update GUI.
	 *///####[73]####
    private void setUpTimer() {//####[73]####
        action = new ActionListener() {//####[73]####
//####[75]####
            public void actionPerformed(ActionEvent arg0) {//####[75]####
                if (timerCount == 0) //####[76]####
                {//####[76]####
                    Schedule schedule = _openSchedules.peek();//####[77]####
                    _data.setCurrentSchedule(schedule);//####[78]####
                    _data.updateTotalNumberOfCreatedSchedules(_openSchedules.size() + _closedSchedules.size());//####[79]####
                    _visualiserController.update(false);//####[80]####
                } else {//####[81]####
                    timerCount--;//####[82]####
                }//####[83]####
            }//####[84]####
        };//####[84]####
        _timer = new Timer(1, action);//####[86]####
        _timer.setInitialDelay(0);//####[87]####
        _timer.start();//####[88]####
    }//####[89]####
//####[96]####
    /**
	 * This method returns the optimal schedule
	 * @return void
	 * @throws Exception
	 *///####[96]####
    public Schedule getOptimalSchedule() {//####[96]####
        this.addRootVerticesSchedulesToOpenSchedule();//####[97]####
        Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();//####[98]####
        if (_visualisation) //####[99]####
        {//####[99]####
            _timer.stop();//####[100]####
            _data.setCurrentSchedule(optimalSchedule);//####[101]####
            _data.updateTotalNumberOfCreatedSchedules(_openSchedules.size() + _closedSchedules.size());//####[102]####
            _visualiserController.update(true);//####[103]####
        }//####[104]####
        return optimalSchedule;//####[105]####
    }//####[106]####
//####[115]####
    /**
	 * This method uses the A* algorithm to create schedules 
	 * It only returns back once it finds an optimal schedule
	 * It throws an exception if the openschedule queue is empty because that is not suppose to happen
	 * 
	 * @return optimal schedule
	 *///####[115]####
    private Schedule makeSchedulesUsingAlgorithm() {//####[115]####
        if (_numberOfCores == -1) //####[116]####
        {//####[116]####
            while (_finalSchedules.isEmpty()) //####[117]####
            {//####[117]####
                searchAndExpand();//####[118]####
            }//####[119]####
            return _finalSchedules.poll();//####[120]####
        } else {//####[121]####
            while ((_finalSchedules.size() < _numberOfCores) && (_openSchedules.size() < _numberOfCores)) //####[122]####
            {//####[122]####
                searchAndExpand();//####[123]####
            }//####[124]####
            if (_finalSchedules.size() < _numberOfCores) //####[125]####
            {//####[125]####
                TaskIDGroup g = paralleliseSearch(_openSchedules);//####[126]####
                try {//####[127]####
                    g.waitTillFinished();//####[128]####
                } catch (ExecutionException e) {//####[129]####
                    e.printStackTrace();//####[130]####
                } catch (InterruptedException e) {//####[131]####
                    e.printStackTrace();//####[132]####
                }//####[133]####
            }//####[134]####
            return _finalSchedules.poll();//####[135]####
        }//####[136]####
    }//####[137]####
//####[146]####
    /**
	 * Gets the head of the priority queue and expands it to the children schedules.
	 * If the optimal schedule is found, it stores it to the final schedule list.
	 * if not found, the children schedules get added to the open schedules list 
	 * if pass the conditions required.
	 * 
	 *///####[146]####
    private void searchAndExpand() {//####[146]####
        Schedule currentSchedule = _openSchedules.poll();//####[147]####
        _closedSchedules.add(currentSchedule);//####[148]####
        if (this.hasScheduleUsedAllPossibleVertices(currentSchedule)) //####[149]####
        {//####[149]####
            _finalSchedules.add(currentSchedule);//####[150]####
            return;//####[151]####
        }//####[152]####
        this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);//####[153]####
    }//####[154]####
//####[166]####
    private static volatile Method __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = null;//####[166]####
    private synchronized static void __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet() {//####[166]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[166]####
            try {//####[166]####
                __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__paralleliseSearch", new Class[] {//####[166]####
                    PriorityBlockingQueue.class//####[166]####
                });//####[166]####
            } catch (Exception e) {//####[166]####
                e.printStackTrace();//####[166]####
            }//####[166]####
        }//####[166]####
    }//####[166]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[166]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[166]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[166]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[166]####
    }//####[166]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[166]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules, TaskInfo taskinfo) {//####[166]####
        // ensure Method variable is set//####[166]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[166]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[166]####
        }//####[166]####
        taskinfo.setParameters(_openSchedules);//####[166]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[166]####
        taskinfo.setInstance(this);//####[166]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[166]####
    }//####[166]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[166]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[166]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[166]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[166]####
    }//####[166]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[166]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[166]####
        // ensure Method variable is set//####[166]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[166]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[166]####
        }//####[166]####
        taskinfo.setTaskIdArgIndexes(0);//####[166]####
        taskinfo.addDependsOn(_openSchedules);//####[166]####
        taskinfo.setParameters(_openSchedules);//####[166]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[166]####
        taskinfo.setInstance(this);//####[166]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[166]####
    }//####[166]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[166]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[166]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[166]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[166]####
    }//####[166]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[166]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[166]####
        // ensure Method variable is set//####[166]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[166]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[166]####
        }//####[166]####
        taskinfo.setQueueArgIndexes(0);//####[166]####
        taskinfo.setIsPipeline(true);//####[166]####
        taskinfo.setParameters(_openSchedules);//####[166]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[166]####
        taskinfo.setInstance(this);//####[166]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[166]####
    }//####[166]####
    /**
	 * Parallelises searchAndExpand method to run a number of threads in execution.
	 * _numberOfCores specifies the number of cores that user selected to use in parallel.
	 * if the number is greater than system specification, only the number of threads that
	 * are available will be created and used for searching the head schedule and expand to
	 * children schedules.
	 * 
	 * The search keeps running unless the optimal schedule is found.
	 * 
	 *///####[166]####
    public void __pt__paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[166]####
        while (_finalSchedules.size() < _numberOfCores) //####[167]####
        {//####[167]####
            searchAndExpand();//####[168]####
        }//####[169]####
    }//####[170]####
//####[170]####
//####[180]####
    /**
	 * For the current schedule we are processing,
	 * it tries to find successor schedules that are available
	 * Those successors schedules are then added to the open schedule if it
	 * passes the conditions required 
	 *
	 * @param currentSchedule
	 *///####[180]####
    private void addCurrentSchedulePossibleSuccessorsToOpenSchedule(Schedule currentSchedule) {//####[180]####
        List<Vertex> currentVertexSuccessors = currentSchedule.getChildVertices();//####[181]####
        for (Vertex childVertex : currentVertexSuccessors) //####[182]####
        {//####[182]####
            Schedule currentScheduleCopy = new Schedule(currentSchedule);//####[184]####
            Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];//####[185]####
            currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);//####[187]####
            CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();//####[189]####
            int parentScheduleCost = costFunctionCalculator.getTotalCostFunction(currentSchedule);//####[190]####
            if (_partialExpanded.contains(currentSchedule)) //####[192]####
            {//####[192]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[193]####
                {//####[193]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[194]####
                    if (childScheduleCost <= _upperBoundCost) //####[196]####
                    {//####[196]####
                        if (parentScheduleCost > costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i])) //####[197]####
                        {//####[197]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[198]####
                            {//####[198]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[199]####
                            }//####[200]####
                        }//####[201]####
                    }//####[202]####
                }//####[203]####
            } else {//####[204]####
                boolean partialExpanded = false;//####[205]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[206]####
                {//####[206]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[207]####
                    if (parentScheduleCost >= childScheduleCost) //####[208]####
                    {//####[208]####
                        if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[209]####
                        {//####[209]####
                            _openSchedules.add(currentChildVertexSchedules[i]);//####[210]####
                            partialExpanded = true;//####[211]####
                        }//####[212]####
                    }//####[213]####
                }//####[214]####
                if (!partialExpanded) //####[215]####
                {//####[215]####
                    for (int i = 0; i < _numberOfProcessors; i++) //####[216]####
                    {//####[216]####
                        int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[217]####
                        if (childScheduleCost <= _upperBoundCost) //####[219]####
                        {//####[219]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[220]####
                            {//####[220]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[221]####
                            }//####[222]####
                        }//####[223]####
                    }//####[224]####
                } else {//####[225]####
                    _openSchedules.add(currentSchedule);//####[226]####
                    _closedSchedules.remove(currentSchedule);//####[227]####
                    _partialExpanded.add(currentSchedule);//####[228]####
                }//####[229]####
            }//####[230]####
        }//####[231]####
    }//####[232]####
//####[246]####
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
	 *///####[246]####
    private boolean checkScheduleThroughPruning(Schedule childSchedule) {//####[246]####
        Pruning pruning = new Pruning();//####[247]####
        if (pruning.isCurrentScheduleNeeded(_openSchedules, _closedSchedules, childSchedule)) //####[248]####
        {//####[248]####
            return true;//####[249]####
        }//####[250]####
        return false;//####[251]####
    }//####[253]####
//####[263]####
    /**
	 * This method checks if the current schedule is a finished schedule
	 *
	 * returns true if it is
	 * otherwise returns false
	 *
	 * @param currentSchedule
	 * @return
	 *///####[263]####
    private boolean hasScheduleUsedAllPossibleVertices(Schedule currentSchedule) {//####[263]####
        List<Vertex> currentScheduleUsedVertices = currentSchedule.getAllUsedVertices();//####[264]####
        for (Vertex vertex : Graph.getInstance().getVertices()) //####[265]####
        {//####[265]####
            if (!currentScheduleUsedVertices.contains(vertex)) //####[266]####
            {//####[266]####
                return false;//####[267]####
            }//####[268]####
        }//####[269]####
        return true;//####[270]####
    }//####[271]####
//####[280]####
    /**
	 * This method adds root schedules to openschedules
	 * since at the start of schedule the first _timerTask is the same no matter which processor
	 * it is put on so only one variation of root schedule is added.
	 *
	 *///####[280]####
    private void addRootVerticesSchedulesToOpenSchedule() {//####[280]####
        for (Vertex rootVertex : Graph.getInstance().getRootVertices()) //####[281]####
        {//####[281]####
            Schedule emptySchedule = new Schedule(_numberOfProcessors);//####[282]####
            Schedule[] rootSchedules = new Schedule[_numberOfProcessors];//####[283]####
            rootSchedules = emptySchedule.generateAllPossibleScheduleForSpecifiedVertex(rootVertex);//####[285]####
            _openSchedules.add(rootSchedules[0]);//####[287]####
        }//####[288]####
    }//####[289]####
}//####[289]####
