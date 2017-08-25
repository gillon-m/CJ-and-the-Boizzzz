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
import gui.ScheduleListener;//####[14]####
import gui.VisualiserController;//####[15]####
import heuristics.CostFunctionCalculator;//####[16]####
import pruning.ListScheduling;//####[17]####
import pruning.Pruning;//####[18]####
import components.ScheduleComparator;//####[19]####
import data.Data;//####[20]####
//####[20]####
//-- ParaTask related imports//####[20]####
import pt.runtime.*;//####[20]####
import java.util.concurrent.ExecutionException;//####[20]####
import java.util.concurrent.locks.*;//####[20]####
import java.lang.reflect.*;//####[20]####
import pt.runtime.GuiThread;//####[20]####
import java.util.concurrent.BlockingQueue;//####[20]####
import java.util.ArrayList;//####[20]####
import java.util.List;//####[20]####
//####[20]####
/**
 * This Class uses the Schedule Class and Processor Class
 * to make schedules using the information from the Graph Variable which
 * contains all the list of vertex and edges.
 *
 * The schedules are created using A* algorithm.
 *
 * This Class manages the created schedules and returns the optimal schedule
 *
 * @author Alex Yoo
 *
 *///####[32]####
public class ParallelisedScheduler {//####[33]####
    static{ParaTask.init();}//####[33]####
    /*  ParaTask helper method to access private/protected slots *///####[33]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[33]####
        if (m.getParameterTypes().length == 0)//####[33]####
            m.invoke(instance);//####[33]####
        else if ((m.getParameterTypes().length == 1))//####[33]####
            m.invoke(instance, arg);//####[33]####
        else //####[33]####
            m.invoke(instance, arg, interResult);//####[33]####
    }//####[33]####
//####[34]####
    private int _numberOfProcessors;//####[34]####
//####[35]####
    private PriorityBlockingQueue<Schedule> _openSchedules;//####[35]####
//####[36]####
    private ConcurrentLinkedQueue<Schedule> _closedSchedules;//####[36]####
//####[37]####
    private List<Schedule> _partialExpanded;//####[37]####
//####[38]####
    private List<Schedule> _finalSchedule;//####[38]####
//####[39]####
    private int _upperBoundCost;//####[39]####
//####[40]####
    private int _numberOfCores;//####[40]####
//####[41]####
    private boolean _visualisation;//####[41]####
//####[42]####
    private Timer _timer;//####[42]####
//####[43]####
    private ActionListener action;//####[43]####
//####[44]####
    private int timerCount;//####[44]####
//####[45]####
    private VisualiserController _visualiserController;//####[45]####
//####[46]####
    private Schedule _bestSchedule;//####[46]####
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
        _data = Data.getInstance();//####[59]####
        if (_visualisation) //####[60]####
        {//####[60]####
            _visualiserController = new VisualiserController();//####[61]####
            setUpTimer();//####[62]####
        }//####[63]####
    }//####[64]####
//####[65]####
    private void setUpTimer() {//####[65]####
        action = new ActionListener() {//####[65]####
//####[67]####
            public void actionPerformed(ActionEvent arg0) {//####[67]####
                if (timerCount == 0) //####[68]####
                {//####[68]####
                    Schedule schedule = _openSchedules.peek();//####[69]####
                    _data.setCurrentSchedule(schedule);//####[70]####
                    _data.updateTotalNumberOfCreatedSchedules(_openSchedules.size() + _closedSchedules.size());//####[71]####
                    _visualiserController.update(false);//####[72]####
                } else {//####[73]####
                    timerCount--;//####[74]####
                }//####[75]####
            }//####[76]####
        };//####[76]####
        _timer = new Timer(1, action);//####[80]####
        _timer.setInitialDelay(0);//####[81]####
        _timer.start();//####[82]####
    }//####[83]####
//####[90]####
    /**
	 * This method returns the optimal schedule
	 * @return void
	 * @throws Exception
	 *///####[90]####
    public Schedule getOptimalSchedule() {//####[90]####
        this.addRootVerticesSchedulesToOpenSchedule();//####[91]####
        Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();//####[92]####
        if (_visualisation) //####[93]####
        {//####[93]####
            _timer.stop();//####[94]####
            _data.setCurrentSchedule(optimalSchedule);//####[95]####
            _data.updateTotalNumberOfCreatedSchedules(_openSchedules.size() + _closedSchedules.size());//####[96]####
            _visualiserController.update(true);//####[97]####
        }//####[98]####
        return optimalSchedule;//####[99]####
    }//####[100]####
//####[109]####
    /**
	 * This method uses the A* algorithm to create schedules 
	 * It only returns back once it finds an optimal schedule
	 * It throws an exception if the openschedule queue is empty because that is not suppose to happen
	 * 
	 * @return optimal schedule
	 *///####[109]####
    private Schedule makeSchedulesUsingAlgorithm() {//####[109]####
        if (_numberOfCores == -1) //####[110]####
        {//####[110]####
            while (_finalSchedule.isEmpty()) //####[111]####
            {//####[111]####
                searchAndExpand();//####[112]####
            }//####[113]####
            return _finalSchedule.get(0);//####[114]####
        } else {//####[115]####
            while (_finalSchedule.isEmpty() && (_openSchedules.size() < _numberOfCores)) //####[116]####
            {//####[116]####
                searchAndExpand();//####[117]####
            }//####[118]####
            if (_finalSchedule.isEmpty()) //####[119]####
            {//####[119]####
                TaskIDGroup g = paralleliseSearch(_openSchedules);//####[120]####
                try {//####[121]####
                    g.waitTillFinished();//####[122]####
                } catch (ExecutionException e) {//####[123]####
                    e.printStackTrace();//####[124]####
                } catch (InterruptedException e) {//####[125]####
                    e.printStackTrace();//####[126]####
                }//####[127]####
            }//####[128]####
            return _finalSchedule.get(0);//####[129]####
        }//####[130]####
    }//####[131]####
//####[132]####
    private void searchAndExpand() {//####[132]####
        Schedule currentSchedule = _openSchedules.poll();//####[133]####
        _closedSchedules.add(currentSchedule);//####[134]####
        if (this.hasScheduleUsedAllPossibleVertices(currentSchedule)) //####[135]####
        {//####[135]####
            _finalSchedule.add(currentSchedule);//####[136]####
        }//####[137]####
        this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);//####[138]####
    }//####[139]####
//####[140]####
    private static volatile Method __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = null;//####[140]####
    private synchronized static void __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet() {//####[140]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[140]####
            try {//####[140]####
                __pt__paralleliseSearch_PriorityBlockingQueueSchedule_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__paralleliseSearch", new Class[] {//####[140]####
                    PriorityBlockingQueue.class//####[140]####
                });//####[140]####
            } catch (Exception e) {//####[140]####
                e.printStackTrace();//####[140]####
            }//####[140]####
        }//####[140]####
    }//####[140]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[140]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[140]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[140]####
    }//####[140]####
    private TaskIDGroup<Void> paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules, TaskInfo taskinfo) {//####[140]####
        // ensure Method variable is set//####[140]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[140]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[140]####
        }//####[140]####
        taskinfo.setParameters(_openSchedules);//####[140]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[140]####
        taskinfo.setInstance(this);//####[140]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[140]####
    }//####[140]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[140]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[140]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[140]####
    }//####[140]####
    private TaskIDGroup<Void> paralleliseSearch(TaskID<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[140]####
        // ensure Method variable is set//####[140]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[140]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[140]####
        }//####[140]####
        taskinfo.setTaskIdArgIndexes(0);//####[140]####
        taskinfo.addDependsOn(_openSchedules);//####[140]####
        taskinfo.setParameters(_openSchedules);//####[140]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[140]####
        taskinfo.setInstance(this);//####[140]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[140]####
    }//####[140]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules) {//####[140]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[140]####
        return paralleliseSearch(_openSchedules, new TaskInfo());//####[140]####
    }//####[140]####
    private TaskIDGroup<Void> paralleliseSearch(BlockingQueue<PriorityBlockingQueue<Schedule>> _openSchedules, TaskInfo taskinfo) {//####[140]####
        // ensure Method variable is set//####[140]####
        if (__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method == null) {//####[140]####
            __pt__paralleliseSearch_PriorityBlockingQueueSchedule_ensureMethodVarSet();//####[140]####
        }//####[140]####
        taskinfo.setQueueArgIndexes(0);//####[140]####
        taskinfo.setIsPipeline(true);//####[140]####
        taskinfo.setParameters(_openSchedules);//####[140]####
        taskinfo.setMethod(__pt__paralleliseSearch_PriorityBlockingQueueSchedule_method);//####[140]####
        taskinfo.setInstance(this);//####[140]####
        return TaskpoolFactory.getTaskpool().enqueueMulti(taskinfo, _numberOfCores);//####[140]####
    }//####[140]####
    public void __pt__paralleliseSearch(PriorityBlockingQueue<Schedule> _openSchedules) {//####[140]####
        while (_finalSchedule.isEmpty()) //####[141]####
        {//####[141]####
            searchAndExpand();//####[142]####
        }//####[143]####
    }//####[144]####
//####[144]####
//####[154]####
    /**
	 * For the current schedule we are processing,
	 * it tries to find successor schedules that are available
	 * Those successors schedules are then added to the open schedule if it
	 * passes the conditions required 
	 *
	 * @param currentSchedule
	 *///####[154]####
    private void addCurrentSchedulePossibleSuccessorsToOpenSchedule(Schedule currentSchedule) {//####[154]####
        List<Vertex> currentVertexSuccessors = currentSchedule.getChildVertices();//####[155]####
        for (Vertex childVertex : currentVertexSuccessors) //####[156]####
        {//####[156]####
            Schedule currentScheduleCopy = new Schedule(currentSchedule);//####[158]####
            Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];//####[159]####
            currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);//####[161]####
            CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();//####[163]####
            int parentScheduleCost = costFunctionCalculator.getTotalCostFunction(currentSchedule);//####[164]####
            if (_partialExpanded.contains(currentSchedule)) //####[166]####
            {//####[166]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[167]####
                {//####[167]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[168]####
                    if (childScheduleCost <= _upperBoundCost) //####[170]####
                    {//####[170]####
                        if (parentScheduleCost > costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i])) //####[171]####
                        {//####[171]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[172]####
                            {//####[172]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[173]####
                            }//####[174]####
                        }//####[175]####
                    }//####[176]####
                }//####[177]####
            } else {//####[178]####
                boolean partialExpanded = false;//####[179]####
                for (int i = 0; i < _numberOfProcessors; i++) //####[180]####
                {//####[180]####
                    int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[181]####
                    if (parentScheduleCost >= childScheduleCost) //####[182]####
                    {//####[182]####
                        if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[183]####
                        {//####[183]####
                            _openSchedules.add(currentChildVertexSchedules[i]);//####[184]####
                            partialExpanded = true;//####[185]####
                        }//####[186]####
                    }//####[187]####
                }//####[188]####
                if (!partialExpanded) //####[189]####
                {//####[189]####
                    for (int i = 0; i < _numberOfProcessors; i++) //####[190]####
                    {//####[190]####
                        int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);//####[191]####
                        if (childScheduleCost <= _upperBoundCost) //####[193]####
                        {//####[193]####
                            if (this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) //####[194]####
                            {//####[194]####
                                _openSchedules.add(currentChildVertexSchedules[i]);//####[195]####
                            }//####[196]####
                        }//####[197]####
                    }//####[198]####
                } else {//####[199]####
                    _openSchedules.add(currentSchedule);//####[200]####
                    _closedSchedules.remove(currentSchedule);//####[201]####
                    _partialExpanded.add(currentSchedule);//####[202]####
                }//####[203]####
            }//####[204]####
        }//####[205]####
    }//####[206]####
//####[220]####
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
	 *///####[220]####
    private boolean checkScheduleThroughPruning(Schedule childSchedule) {//####[220]####
        Pruning pruning = new Pruning();//####[221]####
        if (pruning.isCurrentScheduleNeeded(_openSchedules, _closedSchedules, childSchedule)) //####[222]####
        {//####[222]####
            return true;//####[223]####
        }//####[224]####
        return false;//####[225]####
    }//####[227]####
//####[237]####
    /**
	 * This method checks if the current schedule is a finished schedule
	 *
	 * returns true if it is
	 * otherwise returns false
	 *
	 * @param currentSchedule
	 * @return
	 *///####[237]####
    private boolean hasScheduleUsedAllPossibleVertices(Schedule currentSchedule) {//####[237]####
        List<Vertex> currentScheduleUsedVertices = currentSchedule.getAllUsedVertices();//####[238]####
        for (Vertex vertex : Graph.getInstance().getVertices()) //####[239]####
        {//####[239]####
            if (!currentScheduleUsedVertices.contains(vertex)) //####[240]####
            {//####[240]####
                return false;//####[241]####
            }//####[242]####
        }//####[243]####
        return true;//####[244]####
    }//####[245]####
//####[254]####
    /**
	 * This method adds root schedules to openschedules
	 * since at the start of schedule the first _timerTask is the same no matter which processor
	 * it is put on so only one variation of root schedule is added.
	 *
	 *///####[254]####
    private void addRootVerticesSchedulesToOpenSchedule() {//####[254]####
        for (Vertex rootVertex : Graph.getInstance().getRootVertices()) //####[255]####
        {//####[255]####
            Schedule emptySchedule = new Schedule(_numberOfProcessors);//####[256]####
            Schedule[] rootSchedules = new Schedule[_numberOfProcessors];//####[257]####
            rootSchedules = emptySchedule.generateAllPossibleScheduleForSpecifiedVertex(rootVertex);//####[259]####
            _openSchedules.add(rootSchedules[0]);//####[261]####
        }//####[262]####
    }//####[263]####
}//####[263]####
